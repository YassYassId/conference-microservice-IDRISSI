package com.yassine.conferenceservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private String id;
    private Date reviewDate;
    private String comments;
    private Integer score;
}
