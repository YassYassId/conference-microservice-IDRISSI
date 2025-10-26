package com.yassine.keynoteservice.services;

import com.yassine.keynoteservice.dtos.KeynoteRequest;
import com.yassine.keynoteservice.dtos.KeynoteResponse;
import com.yassine.keynoteservice.entities.Keynote;
import com.yassine.keynoteservice.exceptions.KeynoteAlreadyExistsException;
import com.yassine.keynoteservice.exceptions.KeynoteNotFoundException;
import com.yassine.keynoteservice.repository.KeynoteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
@ExtendWith(MockitoExtension.class)
class KeynoteServiceImplTest {

    @Mock
    private KeynoteRepository keynoteRepository;

    @InjectMocks
    private KeynoteServiceImpl keynoteService;

    private KeynoteRequest sampleRequest() {
        return KeynoteRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .function("Speaker")
                .build();
    }

    private Keynote sampleEntity(String id) {
        return Keynote.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .function("Speaker")
                .build();
    }

    @Test
    void createKeynote_success() {
        KeynoteRequest req = sampleRequest();

        when(keynoteRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(keynoteRepository.save(any(Keynote.class)))
                .thenAnswer(invocation -> {
                    Keynote k = invocation.getArgument(0);
                    if (k.getId() == null || k.getId().isBlank()) {
                        k.setId(UUID.randomUUID().toString());
                    }
                    return k;
                });

        KeynoteResponse resp = keynoteService.createKeynote(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getId()).isNotBlank();
        assertThat(resp.getEmail()).isEqualTo(req.getEmail());
        verify(keynoteRepository).existsByEmail(req.getEmail());
        verify(keynoteRepository).save(any(Keynote.class));
    }

    @Test
    void createKeynote_duplicateEmail_throws() {
        KeynoteRequest req = sampleRequest();
        when(keynoteRepository.existsByEmail(req.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> keynoteService.createKeynote(req))
                .isInstanceOf(KeynoteAlreadyExistsException.class)
                .hasMessageContaining(req.getEmail());

        verify(keynoteRepository).existsByEmail(req.getEmail());
        verify(keynoteRepository, never()).save(any());
    }

    @Test
    void getKeynoteById_success() {
        String id = UUID.randomUUID().toString();
        Keynote entity = sampleEntity(id);
        when(keynoteRepository.findById(id)).thenReturn(Optional.of(entity));

        KeynoteResponse resp = keynoteService.getKeynoteById(id);
        assertThat(resp).isNotNull();
        assertThat(resp.getId()).isEqualTo(id);
    }

    @Test
    void getKeynoteById_notFound_throws() {
        String id = "missing-id";
        when(keynoteRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> keynoteService.getKeynoteById(id))
                .isInstanceOf(KeynoteNotFoundException.class)
                .hasMessageContaining(id);
    }

    @Test
    void getAllKeynotes_returnsList() {
        Keynote a = sampleEntity("id-1");
        Keynote b = sampleEntity("id-2");
        when(keynoteRepository.findAll()).thenReturn(List.of(a, b));

        List<KeynoteResponse> list = keynoteService.getAllKeynotes();
        assertThat(list).hasSize(2);
        assertThat(list).extracting(KeynoteResponse::getId).containsExactly("id-1", "id-2");
    }

    @Test
    void updateKeynote_success() {
        String id = UUID.randomUUID().toString();
        Keynote existing = sampleEntity(id); // email = "john.doe@example.com"

        KeynoteRequest request = KeynoteRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com") // ✅ Different email
                .function("Keynote")
                .build();

        when(keynoteRepository.findById(id)).thenReturn(Optional.of(existing));
        // Stub with concrete values to avoid "unnecessary stubbing"
        when(keynoteRepository.existsByEmailAndIdNot("jane.doe@example.com", id)).thenReturn(false);
        when(keynoteRepository.save(any(Keynote.class))).thenAnswer(inv -> inv.getArgument(0));

        KeynoteResponse updated = keynoteService.updateKeynote(id, request);

        assertThat(updated.getFirstName()).isEqualTo("Jane");
        assertThat(updated.getEmail()).isEqualTo("jane.doe@example.com");
        verify(keynoteRepository).existsByEmailAndIdNot("jane.doe@example.com", id);
    }

    @Test
    void updateKeynote_partialUpdate_success() {
        String id = UUID.randomUUID().toString();
        Keynote existing = Keynote.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .function("Speaker")
                .build();

        KeynoteRequest partial = KeynoteRequest.builder()
                .function("Lead Speaker")
                .build(); // only function

        when(keynoteRepository.findById(id)).thenReturn(Optional.of(existing));
        when(keynoteRepository.save(any(Keynote.class))).thenAnswer(inv -> inv.getArgument(0));

        KeynoteResponse result = keynoteService.updateKeynote(id, partial);
        assertThat(result.getFunction()).isEqualTo("Lead Speaker");
        assertThat(result.getFirstName()).isEqualTo("John"); // unchanged
    }

    @Test
    void updateKeynote_notFound_throws() {
        when(keynoteRepository.findById("no-id")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> keynoteService.updateKeynote("no-id", sampleRequest()))
                .isInstanceOf(KeynoteNotFoundException.class);
    }

    @Test
    void deleteKeynote_success() {
        String id = "to-delete";
        when(keynoteRepository.existsById(id)).thenReturn(true);
        keynoteService.deleteKeynote(id);
        verify(keynoteRepository).deleteById(id);
    }

    @Test
    void deleteKeynote_notFound_throws() {
        when(keynoteRepository.existsById("missing")).thenReturn(false);
        assertThatThrownBy(() -> keynoteService.deleteKeynote("missing"))
                .isInstanceOf(KeynoteNotFoundException.class);
        verify(keynoteRepository, never()).deleteById(any());
    }

    @Test
    void createKeynote_nullRequest_throws() {
        assertThatThrownBy(() -> keynoteService.createKeynote(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createKeynote_blankEmail_throws() {
        KeynoteRequest bad = KeynoteRequest.builder()
                .firstName("A")
                .lastName("B")
                .email("")
                .function("C")
                .build();
        assertThatThrownBy(() -> keynoteService.createKeynote(bad))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");
    }
}
