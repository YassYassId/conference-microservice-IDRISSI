package com.yassine.keynoteservice.services;

import com.yassine.keynoteservice.dtos.KeynoteRequest;
import com.yassine.keynoteservice.dtos.KeynoteResponse;
import com.yassine.keynoteservice.entities.Keynote;

import java.util.List;

public interface KeynoteService {
    KeynoteResponse createKeynote(KeynoteRequest request);
    KeynoteResponse getKeynoteById(String id);
    List<KeynoteResponse> getAllKeynotes();
    KeynoteResponse updateKeynote(String id,KeynoteRequest request);
    void deleteKeynote(String id);


}
