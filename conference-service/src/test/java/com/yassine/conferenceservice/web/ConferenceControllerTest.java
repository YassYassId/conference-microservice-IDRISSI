package com.yassine.conferenceservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yassine.conferenceservice.dtos.ConferenceRequest;
import com.yassine.conferenceservice.dtos.ReviewRequest;
import com.yassine.conferenceservice.enums.ConfType;
import com.yassine.conferenceservice.services.ConferenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConferenceRestController.class)
class ConferenceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Use @MockBean to mock the service bean and inject it into the Spring context (and thus the controller)
    @MockitoBean
    private ConferenceService conferenceService;

    @Test
    void createConference_returns201() throws Exception {
        ConferenceRequest request = ConferenceRequest.builder()
                .title("DevConf")
                .type(ConfType.COMMERCIAL)
                .startDate(new Date())
                .duration(1.5)
                .nbParticipants(100)
                .build();

        // Mock the service method call to return a successful response
        given(conferenceService.createConference(any()))
                .willReturn(
                        com.yassine.conferenceservice.dtos.ConferenceResponse.builder()
                                .id("conf-1")
                                .title("DevConf")
                                .reviews(java.util.List.of())
                                .keynotes(java.util.List.of())
                                .build()
                );

        mockMvc.perform(post("/v1/conferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // Expect 201 Created
                .andExpect(jsonPath("$.id").value("conf-1"));
    }

    @Test
    void addReview_returns201() throws Exception {
        String confId = "conf-1";
        ReviewRequest request = ReviewRequest.builder()
                .score(4)
                .comments("Very good")
                .build();

        // Mock the service method call
        given(conferenceService.addReview(eq(confId), any()))
                .willReturn(
                        com.yassine.conferenceservice.dtos.ReviewResponse.builder()
                                .id("review-1")
                                .score(4)
                                .comments("Very good")
                                .build()
                );

        mockMvc.perform(post("/v1/conferences/{id}/reviews", confId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // Expect 201 Created
                .andExpect(jsonPath("$.score").value(4));
    }

    @Test
    void getConference_returns200() throws Exception {
        String id = "conf-1";
        // Mock the service method call
        given(conferenceService.getConferenceById(id))
                .willReturn(
                        com.yassine.conferenceservice.dtos.ConferenceResponse.builder()
                                .id(id)
                                .title("Test Conf")
                                .reviews(java.util.List.of())
                                .keynotes(java.util.List.of())
                                .build()
                );

        mockMvc.perform(get("/v1/conferences/{id}", id))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(jsonPath("$.id").value(id));
    }
}
