package com.yassine.conferenceservice.projections;

import com.yassine.conferenceservice.entities.Conference;
import com.yassine.conferenceservice.entities.Review;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Projection(name = "fullConference", types = Conference.class)
public interface ConferenceProjection {
    String getId();
    String getTitle();
    String getType();
    Date getStartDate();
    Double getDuration();
    Integer getNbParticipants();
    Double getScore();
    List<Review> getReviews();
}
