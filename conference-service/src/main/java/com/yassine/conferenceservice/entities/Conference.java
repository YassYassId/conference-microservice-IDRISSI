package com.yassine.conferenceservice.entities;


import com.yassine.conferenceservice.enums.confType;
import com.yassine.conferenceservice.model.Keynote;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Conference {

    @Id
    private String id;
    private String title;
    private confType type;
    private Date startDate;
    private Double duration;
    private Integer nbParticipants;
    private Double score;
    @OneToMany(mappedBy = "conference")
    private List<Review> reviews = new ArrayList<>();

    @Transient
    private List<Keynote> keynotes = new ArrayList<>();
}
