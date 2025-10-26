package com.yassine.keynoteservice.services;

import com.yassine.keynoteservice.dtos.KeynoteRequest;
import com.yassine.keynoteservice.dtos.KeynoteResponse;
import com.yassine.keynoteservice.entities.Keynote;
import com.yassine.keynoteservice.exceptions.KeynoteAlreadyExistsException;
import com.yassine.keynoteservice.exceptions.KeynoteNotFoundException;
import com.yassine.keynoteservice.mappers.KeynoteMapper;
import com.yassine.keynoteservice.repository.KeynoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class KeynoteServiceImpl implements KeynoteService {

    private final KeynoteRepository keynoteRepository;

    @Override
    public KeynoteResponse createKeynote(KeynoteRequest request) {
        validateCreateRequest(request);

        if (keynoteRepository.existsByEmail(request.getEmail())) {
            throw new KeynoteAlreadyExistsException(request.getEmail());
        }

        Keynote keynote = KeynoteMapper.toEntity(request);
        if (keynote.getId() == null || keynote.getId().isBlank()) {
            keynote.setId(UUID.randomUUID().toString());
        }

        Keynote savedKeynote = keynoteRepository.save(keynote);
        return KeynoteMapper.toResponse(savedKeynote);
    }

    @Override
    @Transactional(readOnly = true)
    public KeynoteResponse getKeynoteById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Keynote ID must not be null or empty.");
        }

        Keynote keynote = keynoteRepository.findById(id)
                .orElseThrow(() -> new KeynoteNotFoundException(id));

        return KeynoteMapper.toResponse(keynote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KeynoteResponse> getAllKeynotes() {
        List<Keynote> keynotes = keynoteRepository.findAll();
        return KeynoteMapper.toResponseList(keynotes);
    }

    @Override
    public KeynoteResponse updateKeynote(String id, KeynoteRequest request) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Keynote ID must not be null or empty.");
        }
        validateUpdateRequest(request); // Use partial validation

        Keynote existingKeynote = keynoteRepository.findById(id)
                .orElseThrow(() -> new KeynoteNotFoundException(id));

        // If email is being updated, check for duplicates (excluding current)
        if (request.getEmail() != null && !request.getEmail().equals(existingKeynote.getEmail())) {
            if (keynoteRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new KeynoteAlreadyExistsException(request.getEmail());
            }
        }

        KeynoteMapper.updateEntityFromRequest(request, existingKeynote);
        Keynote updatedKeynote = keynoteRepository.save(existingKeynote);
        return KeynoteMapper.toResponse(updatedKeynote);
    }

    @Override
    public void deleteKeynote(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Keynote ID must not be null or empty.");
        }

        if (!keynoteRepository.existsById(id)) {
            throw new KeynoteNotFoundException(id);
        }

        keynoteRepository.deleteById(id);
    }

    // Validation for CREATE (all fields required)
    private void validateCreateRequest(KeynoteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Keynote request must not be null.");
        }
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (request.getLastName() == null || request.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (!request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email must be valid.");
        }
        if (request.getFunction() == null || request.getFunction().isBlank()) {
            throw new IllegalArgumentException("Function is required.");
        }
    }

    // Validation for UPDATE (only validate provided fields)
    private void validateUpdateRequest(KeynoteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Update request must not be null.");
        }

        // Only validate if the field is provided (non-null)
        if (request.getFirstName() != null && request.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name must not be blank if provided.");
        }
        if (request.getLastName() != null && request.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name must not be blank if provided.");
        }
        if (request.getEmail() != null) {
            if (request.getEmail().isBlank()) {
                throw new IllegalArgumentException("Email must not be blank if provided.");
            }
            if (!request.getEmail().contains("@")) {
                throw new IllegalArgumentException("Email must be valid if provided.");
            }
        }
        if (request.getFunction() != null && request.getFunction().isBlank()) {
            throw new IllegalArgumentException("Function must not be blank if provided.");
        }
    }
}
