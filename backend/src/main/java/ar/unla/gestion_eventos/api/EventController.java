package ar.unla.gestion_eventos.api;

import ar.unla.gestion_eventos.Service.EventService;
import ar.unla.gestion_eventos.dto.UpdateEventRequest;
import ar.unla.gestion_eventos.dto.UpdateEventResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PutMapping("/{glpiTicketId}")
    public ResponseEntity<UpdateEventResponse> update(@PathVariable long glpiTicketId,
                                                      @Valid @RequestBody UpdateEventRequest request) {
        var resp = eventService.updateByGlpiTicketId(glpiTicketId, request);
        return ResponseEntity.ok(resp);
    }
}
