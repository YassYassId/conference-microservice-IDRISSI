package com.yassine.keynoteservice.controllers;

import com.yassine.keynoteservice.dtos.ErrorResponse;
import com.yassine.keynoteservice.dtos.KeynoteRequest;
import com.yassine.keynoteservice.dtos.KeynoteResponse;
import com.yassine.keynoteservice.exceptions.KeynoteAlreadyExistsException;
import com.yassine.keynoteservice.exceptions.KeynoteNotFoundException;
import com.yassine.keynoteservice.services.KeynoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/keynotes")
@RequiredArgsConstructor
public class KeynoteController {
    private final KeynoteService keynoteService;

    @PostMapping
    public ResponseEntity<KeynoteResponse> createKeynote(@RequestBody KeynoteRequest request) {
        KeynoteResponse response = keynoteService.createKeynote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KeynoteResponse> getKeynoteById(@PathVariable String id) {
        KeynoteResponse response = keynoteService.getKeynoteById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<KeynoteResponse>> getAllKeynotes() {
        List<KeynoteResponse> responses = keynoteService.getAllKeynotes();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KeynoteResponse> updateKeynote(
            @PathVariable String id,
            @RequestBody KeynoteRequest request) {
        KeynoteResponse response = keynoteService.updateKeynote(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKeynote(@PathVariable String id) {
        keynoteService.deleteKeynote(id);
    }

    // --- Exception Handlers (Fallbacks) ---

    @ExceptionHandler(KeynoteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleKeynoteNotFound(KeynoteNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Keynote Not Found",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(KeynoteAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleKeynoteAlreadyExists(KeynoteAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // Log the actual exception (use SLF4J in real apps)
        System.err.println("Unexpected error: " + ex.getMessage());
        ex.printStackTrace();

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
