package ar.unla.gestion_eventos.Controller;

import ar.unla.gestion_eventos.Domain.EventType;
import ar.unla.gestion_eventos.Service.EventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/event-types")
public class EventTypeController {

    private final EventTypeService eventTypeService;

    @Autowired
    public EventTypeController(EventTypeService eventTypeService) {
        this.eventTypeService = eventTypeService;
    }

    // CREATE - POST /admin/event-types
    @PostMapping
    public ResponseEntity<EventType> createEventType(@RequestBody EventType eventType) {
        EventType newEventType = eventTypeService.createEventType(eventType);
        return ResponseEntity.created(URI.create("/admin/event-types/" + newEventType.getId())).body(newEventType);
    }

    // READ ALL - GET /admin/event-types
    @GetMapping
    public List<EventType> getAllEventTypes() {
        return eventTypeService.findAllEventTypes();
    }

    // READ ONE - GET /admin/event-types/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EventType> getEventTypeById(@PathVariable Long id) {
        Optional<EventType> eventType = eventTypeService.findEventTypeById(id);
        return eventType.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // UPDATE - PUT /admin/event-types/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EventType> updateEventType(@PathVariable Long id, @RequestBody EventType updatedEventType) {
        Optional<EventType> eventType = eventTypeService.updateEventType(id, updatedEventType);
        return eventType.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE - DELETE /admin/event-types/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventType(@PathVariable Long id) {
        if (eventTypeService.findEventTypeById(id).isPresent()) {
            eventTypeService.deleteEventType(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}