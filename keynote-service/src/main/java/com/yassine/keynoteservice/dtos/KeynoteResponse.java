package com.yassine.keynoteservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeynoteResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String function;
}
