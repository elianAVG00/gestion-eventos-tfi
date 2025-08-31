package ar.unla.gestion_eventos.api;

import ar.unla.gestion_eventos.Service.EventNoteService;
import ar.unla.gestion_eventos.dto.CreateEventNoteRequest;
import ar.unla.gestion_eventos.dto.CreateEventNoteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events/{glpiTicketId}/notes")
@RequiredArgsConstructor
public class EventNoteController {
    private final EventNoteService service;

    @PostMapping
    public ResponseEntity<CreateEventNoteResponse> addNote(@PathVariable long glpiTicketId,
                                                           @Valid @RequestBody CreateEventNoteRequest request) {
        var resp = service.addToTicket(glpiTicketId, request);
        return ResponseEntity.ok(resp);
    }
}