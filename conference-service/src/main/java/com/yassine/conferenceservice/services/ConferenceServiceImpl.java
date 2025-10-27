package com.yassine.conferenceservice.services;

import com.yassine.conferenceservice.dtos.ConferenceRequest;
import com.yassine.conferenceservice.dtos.ConferenceResponse;
import com.yassine.conferenceservice.dtos.ReviewRequest;
import com.yassine.conferenceservice.dtos.ReviewResponse;
import com.yassine.conferenceservice.entities.Conference;
import com.yassine.conferenceservice.entities.Review;
import com.yassine.conferenceservice.exceptions.ConferenceNotFoundException;
import com.yassine.conferenceservice.feign.KeynoteRestClient;
import com.yassine.conferenceservice.mappers.ConferenceMapper;
import com.yassine.conferenceservice.model.Keynote;
import com.yassine.conferenceservice.repositories.ConferenceRepository;
import com.yassine.conferenceservice.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConferenceServiceImpl implements ConferenceService {
    private final ConferenceRepository conferenceRepository;
    private final ReviewRepository reviewRepository;
    private final KeynoteRestClient keynoteRestClient;
    private final ConferenceMapper mapper;

    @Override
    public ConferenceResponse createConference(ConferenceRequest request) {
        Conference conference = mapper.toEntity(request);
        if (conference.getId() == null || conference.getId().isBlank()) {
            conference.setId(UUID.randomUUID().toString());
        }
        // Calculate initial score (e.g., 0 or based on participants)
        conference.setScore(0.0);

        Conference saved = conferenceRepository.save(conference);
        return buildResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ConferenceResponse getConferenceById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Conference ID must not be null or empty.");
        }

        Conference conference = conferenceRepository.findById(id)
                .orElseThrow(() -> new ConferenceNotFoundException(id));

        return buildResponse(conference);
    }

    @Override
    public List<ConferenceResponse> getAllConferences() {
        return conferenceRepository.findAll().stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    private ConferenceResponse buildResponse(Conference conference) {
        // Fetch reviews
        List<Review> reviews = reviewRepository.findByConferenceId(conference.getId());
        List<com.yassine.conferenceservice.dtos.ReviewResponse> reviewResponses = mapper.toReviewResponseList(reviews);

        // Fetch keynotes, handling potential Feign errors gracefully
        List<String> keynoteIds = conference.getKeynoteIds();
        List<com.yassine.conferenceservice.model.Keynote> keynotes = new ArrayList<>();
        if (keynoteIds != null) {
            for (String id : keynoteIds) {
                try {
                    Keynote keynote = keynoteRestClient.getKeynoteById(id);
                    keynotes.add(keynote);
                } catch (Exception e) {
                    Keynote fallbackKeynote = new Keynote();
                    fallbackKeynote.setId(id);
                    fallbackKeynote.setFirstName("Unknown");
                    fallbackKeynote.setLastName("Speaker");
                    fallbackKeynote.setEmail("unknown@example.com");
                    fallbackKeynote.setFunction("Unavailable");
                    keynotes.add(fallbackKeynote);
                }
            }
        }

        return mapper.toResponse(conference, reviewResponses, keynotes);
    }

    @Override
    @Transactional
    public ReviewResponse addReview(String conferenceId, ReviewRequest request) {
        validateReviewRequest(request);

        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new ConferenceNotFoundException(conferenceId));

        Review review = mapper.toEntity(request, conference);
        review.setId(UUID.randomUUID().toString());
        Review savedReview = reviewRepository.save(review);

        // Recalculate conference score (average of all reviews)
        List<Review> allReviews = reviewRepository.findByConferenceId(conferenceId);
        double averageScore = allReviews.stream()
                .mapToInt(Review::getScore)
                .average()
                .orElse(0.0);
        conference.setScore(averageScore);
        conferenceRepository.save(conference);

        return mapper.toReviewResponse(savedReview);
    }

    private void validateReviewRequest(ReviewRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Review request must not be null.");
        }
        if (request.getScore() == null || request.getScore() < 1 || request.getScore() > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5.");
        }
        if (request.getComments() == null || request.getComments().isBlank()) {
            throw new IllegalArgumentException("Comments are required.");
        }
    }
}
