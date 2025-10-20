package com.yassine.conferenceservice.web;

import com.yassine.conferenceservice.entities.Conference;
import com.yassine.conferenceservice.entities.Review;
import com.yassine.conferenceservice.feign.KeynoteRestClient;
import com.yassine.conferenceservice.model.Keynote;
import com.yassine.conferenceservice.repositories.ConferenceRepository;
import com.yassine.conferenceservice.repositories.ReviewRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ConferenceRestController {
    @Autowired
    private ConferenceRepository conferenceRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    private final KeynoteRestClient keynoteRestClient;

    public ConferenceRestController(KeynoteRestClient keynoteRestClient) {
        this.keynoteRestClient = keynoteRestClient;
    }

    @GetMapping("/conferences/{id}")
    public Conference getConference(@PathVariable String id) {
        Conference conference = conferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conference not found with id: " + id));

        List<Review> reviews = reviewRepository.findByConferenceId(id);
        conference.setReviews(reviews);

        List<String> keynoteIds = conference.getKeynoteIds();

        List<Keynote> keynotes = keynoteIds.stream()
                .map(keynoteRestClient::getKeynoteById) // call for each ID
                .toList();

        conference.setKeynotes(new ArrayList<>(keynotes));

        return conference;
    }
}
