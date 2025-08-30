package ar.unla.gestion_eventos.Service;

import ar.unla.gestion_eventos.Domain.EventType;
import ar.unla.gestion_eventos.Repository.EventTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventTypeService {

    private final EventTypeRepository eventTypeRepository;

    @Autowired
    public EventTypeService(EventTypeRepository eventTypeRepository) {
        this.eventTypeRepository = eventTypeRepository;
    }

    public EventType createEventType(EventType eventType) {
        return eventTypeRepository.save(eventType);
    }

    public List<EventType> findAllEventTypes() {
        return eventTypeRepository.findAll();
    }

    public Optional<EventType> findEventTypeById(Long id) {
        return eventTypeRepository.findById(id);
    }

    public Optional<EventType> updateEventType(Long id, EventType updatedEventType) {
        return eventTypeRepository.findById(id).map(eventType -> {
            eventType.setName(updatedEventType.getName());
            eventType.setActive(updatedEventType.isActive());
            return eventTypeRepository.save(eventType);
        });
    }

    public void deleteEventType(Long id) {
        eventTypeRepository.deleteById(id);
    }
}