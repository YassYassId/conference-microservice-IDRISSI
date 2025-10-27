package com.yassine.conferenceservice.mappers;


import com.yassine.conferenceservice.dtos.ConferenceRequest;
import com.yassine.conferenceservice.dtos.ConferenceResponse;
import com.yassine.conferenceservice.dtos.ReviewRequest;
import com.yassine.conferenceservice.dtos.ReviewResponse;
import com.yassine.conferenceservice.entities.Conference;
import com.yassine.conferenceservice.entities.Review;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConferenceMapper {
    public Conference toEntity(ConferenceRequest request) {
        return Conference.builder()
                .title(request.getTitle())
                .type(request.getType())
                .startDate(request.getStartDate())
                .duration(request.getDuration())
                .nbParticipants(request.getNbParticipants())
                .keynoteIds(request.getKeynoteIds())
                .build();
    }

    public ConferenceResponse toResponse(
            Conference conference,
            List<ReviewResponse> reviews,
            List<com.yassine.conferenceservice.model.Keynote> keynotes) {

        return ConferenceResponse.builder()
                .id(conference.getId())
                .title(conference.getTitle())
                .type(conference.getType())
                .startDate(conference.getStartDate())
                .duration(conference.getDuration())
                .nbParticipants(conference.getNbParticipants())
                .score(conference.getScore())
                .reviews(reviews)
                .keynotes(keynotes)
                .build();
    }

    public ReviewResponse toReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reviewDate(review.getReviewDate())
                .comments(review.getComments())
                .score(review.getScore())
                .build();
    }

    public List<ReviewResponse> toReviewResponseList(List<Review> reviews) {
        return reviews.stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }

    public Review toEntity(ReviewRequest request, Conference conference) {
        return Review.builder()
                .reviewDate(request.getReviewDate() != null ? request.getReviewDate() : new Date())
                .comments(request.getComments())
                .score(request.getScore())
                .conference(conference)
                .build();
    }
}
