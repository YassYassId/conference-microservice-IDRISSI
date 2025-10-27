package com.yassine.conferenceservice.dtos;

import com.yassine.conferenceservice.enums.ConfType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class ConferenceRequest {
    private String title;
    private ConfType type;
    private Date startDate;
    private Double duration;
    private Integer nbParticipants;
    private List<String> keynoteIds; // only IDs
}
