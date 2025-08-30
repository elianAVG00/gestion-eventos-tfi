package ar.unla.gestion_eventos.Controller;

import ar.unla.gestion_eventos.Domain.Event;
import ar.unla.gestion_eventos.Dto.GlpiEventDto;
import ar.unla.gestion_eventos.Service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/ingest")
    public ResponseEntity<Event> ingestGlpiEvent(@RequestBody GlpiEventDto glpiEventDto) {
        try {
            Event newEvent = eventService.createEventFromGlpi(glpiEventDto);
            return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
        } catch (Exception e) {
            // Manejo de excepciones, por ejemplo, si el DTO es inválido
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
