package com.yassine.keynoteservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor @NoArgsConstructor @Data @Builder
public class Keynote {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String function;

}
