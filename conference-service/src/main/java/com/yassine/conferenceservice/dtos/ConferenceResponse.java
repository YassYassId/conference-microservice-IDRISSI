package com.yassine.conferenceservice.dtos;

import com.yassine.conferenceservice.enums.ConfType;
import com.yassine.conferenceservice.model.Keynote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ConferenceResponse {
    private String id;
    private String title;
    private ConfType type;
    private Date startDate;
    private Double duration;
    private Integer nbParticipants;
    private Double score;
    private List<ReviewResponse> reviews;
    private List<Keynote> keynotes;
}
