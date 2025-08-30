package ar.unla.gestion_eventos.Service;

import ar.unla.gestion_eventos.Domain.EventType;
import ar.unla.gestion_eventos.Repository.EventTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Optional;

@Service
public class EventTypeResolver {

    private final EventTypeRepository eventTypeRepository;

    @Autowired
    public EventTypeResolver(EventTypeRepository eventTypeRepository) {
        this.eventTypeRepository = eventTypeRepository;
    }

    public Optional<EventType> resolveEventType(String glpiName) {
        if (glpiName == null || glpiName.trim().isEmpty()) {
            return Optional.empty();
        }

        String normalizedName = normalizeString(glpiName);

        return eventTypeRepository.findByName(normalizedName);
    }

    private String normalizeString(String text) {
        String result = text.toLowerCase().trim();
        result = Normalizer.normalize(result, Normalizer.Form.NFD);
        result = result.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        result = result.replaceAll("\\s+", " ");
        return result;
    }
}