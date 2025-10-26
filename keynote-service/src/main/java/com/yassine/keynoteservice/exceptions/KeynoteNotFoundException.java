package com.yassine.keynoteservice.exceptions;

public class KeynoteNotFoundException extends RuntimeException {
    public KeynoteNotFoundException(String id) {
        super("Keynote with ID '" + id + "' not found.");
    }
}
