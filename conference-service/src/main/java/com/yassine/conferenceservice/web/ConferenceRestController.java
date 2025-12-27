package com.yassine.conferenceservice.web;

import com.yassine.conferenceservice.dtos.ConferenceRequest;
import com.yassine.conferenceservice.dtos.ConferenceResponse;
import com.yassine.conferenceservice.dtos.ReviewRequest;
import com.yassine.conferenceservice.dtos.ReviewResponse;
import com.yassine.conferenceservice.services.ConferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/conferences")
@RequiredArgsConstructor
public class ConferenceRestController {
    private final ConferenceService conferenceService;

    @PostMapping
    public ResponseEntity<ConferenceResponse> createConference(@RequestBody ConferenceRequest request) {
        ConferenceResponse response = conferenceService.createConference(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConferenceResponse> getConference(@PathVariable String id) {
        ConferenceResponse response = conferenceService.getConferenceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ConferenceResponse>> getAllConferences() {
        List<ConferenceResponse> responses = conferenceService.getAllConferences();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable String id,
            @RequestBody ReviewRequest request) {
        ReviewResponse response = conferenceService.addReview(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
