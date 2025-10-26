package com.yassine.keynoteservice.exceptions;

public class KeynoteAlreadyExistsException extends RuntimeException {
    public KeynoteAlreadyExistsException(String email) {
        super("Keynote with email '" + email + "' already exists.");
    }
}
