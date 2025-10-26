package com.yassine.keynoteservice.mappers;

import com.yassine.keynoteservice.dtos.KeynoteRequest;
import com.yassine.keynoteservice.dtos.KeynoteResponse;
import com.yassine.keynoteservice.entities.Keynote;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeynoteMapper {
    // Convert KeynoteRequest → Keynote (for creation)
    public static Keynote toEntity(KeynoteRequest request) {
        return Keynote.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .function(request.getFunction())
                .build();
    }

    // Convert Keynote → KeynoteResponse
    public static KeynoteResponse toResponse(Keynote keynote) {
        return KeynoteResponse.builder()
                .id(keynote.getId())
                .firstName(keynote.getFirstName())
                .lastName(keynote.getLastName())
                .email(keynote.getEmail())
                .function(keynote.getFunction())
                .build();
    }

    // Convert List<Keynote> → List<KeynoteResponse>
    public static List<KeynoteResponse> toResponseList(List<Keynote> keynotes) {
        return keynotes.stream()
                .map(KeynoteMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Update an existing Keynote entity from a KeynoteRequest (for updates)
    public static void updateEntityFromRequest(KeynoteRequest request, Keynote keynote) {
        if (isNotBlank(request.getFirstName())) {
            keynote.setFirstName(request.getFirstName());
        }
        if (isNotBlank(request.getLastName())) {
            keynote.setLastName(request.getLastName());
        }
        if (isNotBlank(request.getEmail())) {
            keynote.setEmail(request.getEmail());
        }
        if (isNotBlank(request.getFunction())) {
            keynote.setFunction(request.getFunction());
        }
    }

    private static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
