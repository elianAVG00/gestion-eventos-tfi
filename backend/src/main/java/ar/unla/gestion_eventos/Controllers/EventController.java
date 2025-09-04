package ar.unla.gestion_eventos.Controllers;

import ar.unla.gestion_eventos.Domain.Event;
import ar.unla.gestion_eventos.Service.EventNoteService;
import ar.unla.gestion_eventos.Service.EventService;
import ar.unla.gestion_eventos.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventNoteService eventNoteService;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.findAll();
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{glpiTicketId}")
    public ResponseEntity<UpdateEventResponse> update(@PathVariable long glpiTicketId,
                                                      @Valid @RequestBody UpdateEventRequest request) {
        var resp = eventService.updateByGlpiTicketId(glpiTicketId, request);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{glpiTicketId}/notes")
    public ResponseEntity<CreateEventNoteResponse> addNote(@PathVariable long glpiTicketId,
                                                           @Valid @RequestBody CreateEventNoteRequest request) {
        var resp = eventNoteService.addToTicket(glpiTicketId, request);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{glpiTicketId}/notes")
    public ResponseEntity<List<EventNoteDto>> getAllNotesForEvent(@PathVariable long glpiTicketId) {
        var resp = eventNoteService.findAllForEvent(glpiTicketId);
        return ResponseEntity.ok(resp);
    }
}