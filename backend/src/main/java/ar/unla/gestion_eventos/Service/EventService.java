package ar.unla.gestion_eventos.Service;

import ar.unla.gestion_eventos.Domain.Event;
import ar.unla.gestion_eventos.Repository.EventRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ar.unla.gestion_eventos.dto.UpdateEventRequest;
import ar.unla.gestion_eventos.dto.UpdateEventResponse;
import ar.unla.gestion_eventos.glpi.GlpiClient;
import ar.unla.gestion_eventos.glpi.GlpiSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final GlpiClient glpiClient;
    private final GlpiSyncService glpiSyncService;

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

    /** Actualiza un evento por su glpiTicketId y sincroniza con GLPI */
    public UpdateEventResponse updateByGlpiTicketId(long glpiTicketId, UpdateEventRequest req) {
        Event existing = eventRepository.findByGlpiTicketId(String.valueOf(glpiTicketId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));

        Map<String, Object> input = new HashMap<>();
        if (req.getTitle() != null) {
            if (req.getTitle().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El título no puede estar vacío");
            }
            input.put("name", req.getTitle());
        }
        if (req.getDescription() != null) {
            if (req.getDescription().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La descripción no puede estar vacía");
            }
            input.put("content", req.getDescription());
        }
        if (req.getStatusId() != null) {
            input.put("status", req.getStatusId());
        }
        if (req.getPriorityId() != null) {
            input.put("priority", req.getPriorityId());
        }

        if (input.isEmpty()) {
            return new UpdateEventResponse(existing, false, false);
        }

        glpiClient.openSession();
        try {
            String token = glpiClient.getSessionToken();
            GlpiClient.GlpiUpdateResult result = glpiClient.updateTicket(token, glpiTicketId, input);
            if (!result.success()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, result.message());
            }

            Event refreshed = null;
            boolean refreshPending = false;
            try {
                refreshed = glpiSyncService.ingestTicket(glpiTicketId);
            } catch (Exception e) {
                refreshPending = true;
                refreshed = existing;
            }
            return new UpdateEventResponse(refreshed, true, refreshPending);
        } catch (RestClientResponseException e) {
            HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
            if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
                throw new ResponseStatusException(status, "No autorizado en GLPI");
            } else if (status == HttpStatus.UNPROCESSABLE_ENTITY) {
                throw new ResponseStatusException(status, e.getResponseBodyAsString());
            }
            throw e;
        } finally {
            glpiClient.closeSession();
        }
    }
}
