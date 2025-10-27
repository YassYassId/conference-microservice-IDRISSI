package com.yassine.conferenceservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ReviewRequest {
    private Date reviewDate;
    private String comments;
    private Integer score;
}
