package com.yassine.conferenceservice.exceptions;

public class ConferenceNotFoundException extends RuntimeException {
    public ConferenceNotFoundException(String id) {
        super("Conference with ID '" + id + "' not found.");
    }
}
