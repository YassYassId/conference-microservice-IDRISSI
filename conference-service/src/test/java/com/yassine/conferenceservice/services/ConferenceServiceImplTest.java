package com.yassine.conferenceservice.services;

import com.yassine.conferenceservice.dtos.ConferenceRequest;
import com.yassine.conferenceservice.dtos.ConferenceResponse;
import com.yassine.conferenceservice.dtos.ReviewRequest;
import com.yassine.conferenceservice.dtos.ReviewResponse;
import com.yassine.conferenceservice.entities.Conference;
import com.yassine.conferenceservice.entities.Review;
import com.yassine.conferenceservice.enums.ConfType;
import com.yassine.conferenceservice.exceptions.ConferenceNotFoundException;
import com.yassine.conferenceservice.feign.KeynoteRestClient;
import com.yassine.conferenceservice.mappers.ConferenceMapper;
import com.yassine.conferenceservice.model.Keynote;
import com.yassine.conferenceservice.repositories.ConferenceRepository;
import com.yassine.conferenceservice.repositories.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ConferenceServiceImplTest {

    @Mock
    private ConferenceRepository conferenceRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private KeynoteRestClient keynoteRestClient;

    @Spy
    private ConferenceMapper mapper = new ConferenceMapper();

    @InjectMocks
    private ConferenceServiceImpl conferenceService;

    private Conference sampleConference(String id) {
        return Conference.builder()
                .id(id)
                .title("AI Summit")
                .type(ConfType.ACADEMIC)
                .startDate(new Date())
                .duration(2.5)
                .nbParticipants(200)
                .score(0.0)
                .keynoteIds(Arrays.asList("k1", "k2"))
                .build();
    }

    private ConferenceRequest sampleConferenceRequest() {
        ConferenceRequest req = ConferenceRequest.builder()
                        .title("AI Summit")
                        .type(ConfType.ACADEMIC)
                        .startDate(new Date())
                        .duration(2.5)
                        .nbParticipants(200)
                        .keynoteIds(Arrays.asList("k1", "k2"))
                        .build();
        return req;
    }

    @Test
    void createConference_success() {
        ConferenceRequest request = sampleConferenceRequest();
        Conference entity = Conference.builder()
                .id("conf-1")
                .title(request.getTitle())
                .type(request.getType())
                .startDate(request.getStartDate())
                .duration(request.getDuration())
                .nbParticipants(request.getNbParticipants())
                .keynoteIds(request.getKeynoteIds())
                .score(0.0)
                .build();

        when(conferenceRepository.save(any(Conference.class))).thenReturn(entity);

        ConferenceResponse response = conferenceService.createConference(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("conf-1");
        assertThat(response.getTitle()).isEqualTo("AI Summit");
        verify(conferenceRepository).save(any(Conference.class));
    }

    @Test
    void getConferenceById_success() {
        String id = "conf-1";
        Conference conference = sampleConference(id);
        List<Review> reviews = List.of(
                Review.builder().id("r1").score(4).comments("Good").build()
        );
        Keynote k1 = new Keynote();
        k1.setId("k1");
        k1.setFirstName("John");
        Keynote k2 = new Keynote();
        k2.setId("k2");
        k2.setFirstName("Jane");

        when(conferenceRepository.findById(id)).thenReturn(Optional.of(conference));
        when(reviewRepository.findByConferenceId(id)).thenReturn(reviews);
        when(keynoteRestClient.getKeynoteById("k1")).thenReturn(k1);
        when(keynoteRestClient.getKeynoteById("k2")).thenReturn(k2);

        ConferenceResponse response = conferenceService.getConferenceById(id);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getReviews()).hasSize(1);
        assertThat(response.getKeynotes()).hasSize(2);
        assertThat(response.getKeynotes().get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    void getConferenceById_notFound_throws() {
        when(conferenceRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conferenceService.getConferenceById("invalid"))
                .isInstanceOf(ConferenceNotFoundException.class);
    }

    @Test
    void addReview_success() {
        String confId = "conf-1";
        Conference conference = sampleConference(confId);
        ReviewRequest request = ReviewRequest.builder()
                .score(5)
                        .comments("Excellent!")
                                .build();

        Review savedReview = Review.builder()
                .id("review-1")
                .score(5)
                .comments("Excellent!")
                .conference(conference)
                .build();

        // Mock the sequence: find conference -> save review -> find reviews for score calc -> save updated conference
        when(conferenceRepository.findById(confId)).thenReturn(Optional.of(conference));
        when(reviewRepository.findByConferenceId(confId)).thenReturn(List.of(savedReview)); // For score recalculation
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview); // ✅ FIX: Mock the return value of save
        when(conferenceRepository.save(any(Conference.class))).thenReturn(conference); // ✅ FIX: Mock the return value of save

        ReviewResponse response = conferenceService.addReview(confId, request);

        assertThat(response).isNotNull();
        assertThat(response.getScore()).isEqualTo(5); // Assuming single review score becomes conf score initially
        assertThat(response.getComments()).isEqualTo("Excellent!");
        verify(conferenceRepository).save(any(Conference.class)); // Verify conference score was updated
    }

    @Test
    void addReview_conferenceNotFound_throws() {
        when(conferenceRepository.findById("invalid")).thenReturn(Optional.empty());

        ReviewRequest request = ReviewRequest.builder()
                .score(3)
                .comments("Okay")
                .build();

        assertThatThrownBy(() -> conferenceService.addReview("invalid", request))
                .isInstanceOf(ConferenceNotFoundException.class);
    }

    @Test
    void addReview_invalidScore_throws() {
        ReviewRequest request = ReviewRequest.builder()
                        .score(6)
                        .comments("Great")
                        .build();

        assertThatThrownBy(() -> conferenceService.addReview("conf-1", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Score must be between 1 and 5");
    }

    @Test
    void getConferenceById_keynoteServiceDown_usesFallback() {
        String id = "conf-1";
        Conference conference = sampleConference(id);
        conference.setKeynoteIds(List.of("k1"));

        List<Review> reviews = List.of();

        when(conferenceRepository.findById(id)).thenReturn(Optional.of(conference));
        when(reviewRepository.findByConferenceId(id)).thenReturn(reviews);

        when(keynoteRestClient.getKeynoteById("k1")).thenThrow(new RuntimeException("Feign error"));

        ConferenceResponse response = conferenceService.getConferenceById(id);

        assertThat(response).isNotNull();
        assertThat(response.getKeynotes()).hasSize(1);
        assertThat(response.getKeynotes().get(0).getFirstName()).isEqualTo("Unknown");
        assertThat(response.getKeynotes().get(0).getLastName()).isEqualTo("Speaker");
        assertThat(response.getKeynotes().get(0).getEmail()).isEqualTo("unknown@example.com");
        assertThat(response.getKeynotes().get(0).getFunction()).isEqualTo("Unavailable");
    }
}
