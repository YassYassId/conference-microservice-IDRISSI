package com.yassine.conferenceservice.services;

import com.yassine.conferenceservice.dtos.ConferenceRequest;
import com.yassine.conferenceservice.dtos.ConferenceResponse;
import com.yassine.conferenceservice.dtos.ReviewRequest;
import com.yassine.conferenceservice.dtos.ReviewResponse;

import java.util.List;

public interface ConferenceService {
    ConferenceResponse createConference(ConferenceRequest request);
    ConferenceResponse getConferenceById(String id);
    List<ConferenceResponse> getAllConferences();
    ReviewResponse addReview(String conferenceId, ReviewRequest request);
}
