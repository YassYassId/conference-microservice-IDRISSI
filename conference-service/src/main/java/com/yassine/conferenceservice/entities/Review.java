package com.yassine.conferenceservice.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor @AllArgsConstructor @Data @Builder
public class Review {
    @Id
    private String id;
    private Date reviewDate;
    private String comments;
    private Integer score;
    @ManyToOne
    @JoinColumn(name = "conference_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Conference conference;
}
