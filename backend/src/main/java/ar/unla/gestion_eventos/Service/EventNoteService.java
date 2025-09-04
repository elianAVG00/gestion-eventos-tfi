package ar.unla.gestion_eventos.Service;

import ar.unla.gestion_eventos.Domain.Event;
import ar.unla.gestion_eventos.Domain.EventNote;
import ar.unla.gestion_eventos.Repository.EventNoteRepository;
import ar.unla.gestion_eventos.Repository.EventRepository;
import ar.unla.gestion_eventos.dto.*;
import ar.unla.gestion_eventos.glpi.GlpiClient;
import ar.unla.gestion_eventos.glpi.GlpiSyncService;
import jakarta.validation.ValidationException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EventNoteService {
    private final EventRepository eventRepository;
    private final EventNoteRepository noteRepository;
    private final GlpiClient glpiClient;
    private final GlpiSyncService glpiSyncService;

    public CreateEventNoteResponse addToTicket(long glpiTicketId, CreateEventNoteRequest req) {
        if (req.getContent() == null || req.getContent().isBlank()) {
            throw new ValidationException("content is required");
        }

        eventRepository.findByGlpiTicketId(String.valueOf(glpiTicketId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        glpiClient.openSession();
        GlpiClient.GlpiFollowup followup;
        try {
            String token = glpiClient.getSessionToken();
            followup = glpiClient.createEventNote(token, glpiTicketId, req.getContent(), req.getVisibility());
        } finally {
            glpiClient.closeSession();
        }

        EventNote note = EventNote.builder()
                .glpiFollowupId(followup.id())
                .glpiTicketId(glpiTicketId)
                .content(req.getContent())
                .createdAt(followup.createdAt() != null ? followup.createdAt() : Instant.now())
                .author(followup.author())
                .visibility(req.getVisibility())
                .build();
        note = noteRepository.save(note);

        boolean refreshPending = false;
        Event refreshed = null;
        try {
            refreshed = glpiSyncService.ingestTicket(glpiTicketId);
        } catch (Exception ex) {
            refreshPending = true;
        }

        return new CreateEventNoteResponse(
                EventNoteMapper.toDto(note),
                EventDto.from(refreshed),
                refreshPending
        );
    }

    public List<EventNoteDto> findAllForEvent(long glpiTicketId) {
        List<EventNote> noteList = noteRepository.findAllByGlpiTicketId(glpiTicketId);

        if (noteList == null || noteList.isEmpty()) {
            return Collections.emptyList();
        }

        return noteList.stream()
                .map(EventNoteMapper::toDto)
                .collect(Collectors.toList());
    }
}