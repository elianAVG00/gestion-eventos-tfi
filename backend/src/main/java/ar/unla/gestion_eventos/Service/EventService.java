package ar.unla.gestion_eventos.Service;

import ar.unla.gestion_eventos.Domain.*;
import ar.unla.gestion_eventos.Dto.GlpiEventDto; // <-- ¡Esta es la línea que faltaba!
import ar.unla.gestion_eventos.Repository.EventRepository;
import ar.unla.gestion_eventos.Repository.PendingMappingRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final DepartmentResolver departmentResolver;
    private final SpaceResolver spaceResolver;
    private final EventTypeResolver eventTypeResolver;
    private final PendingMappingRepository pendingMappingRepository;

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

    /**
     * Crea un evento a partir de datos de GLPI, resolviendo catálogos.
     * Si un mapeo no se encuentra, lo marca como "incompleto" y lo guarda como pendiente.
     * @param glpiEvent El DTO con los datos del evento de GLPI.
     * @return El evento persistido.
     */
    public Event createEventFromGlpi(GlpiEventDto glpiEvent) {
        Event event = new Event();
        // Mapear los campos básicos del DTO al Event
        event.setTitle(glpiEvent.getTitle());
        event.setGlpiTicketId(glpiEvent.getTicketId());
        event.setStartDateTime(glpiEvent.getStart());
        event.setEndDateTime(glpiEvent.getEnd());
        event.setDescription(glpiEvent.getDescription());
        event.setResponsibleName(glpiEvent.getResponsibleName());
        event.setResponsibleEmail(glpiEvent.getResponsibleEmail());
        event.setResponsiblePhone(glpiEvent.getResponsiblePhone());
        event.setEstimatedAttendees(glpiEvent.getEstimatedAttendees());
        event.setEquipmentNeeded(glpiEvent.getEquipmentNeeded());
        event.setNeedsEarlySetup(glpiEvent.getNeedsEarlySetup());
        event.setNeedsTechnicalAssistance(glpiEvent.getNeedsTechnicalAssistance());
        event.setRecurring(glpiEvent.getRecurring());
        event.setDateMod(glpiEvent.getDateMod());

        boolean hasUnresolvedMapping = false;

        // Intentar resolver Department
        Optional<Department> resolvedDepartment = departmentResolver.resolveDepartment(glpiEvent.getDepartmentName());
        if (resolvedDepartment.isPresent()) {
            event.setDepartment(resolvedDepartment.get());
        } else {
            createPendingMapping("department", glpiEvent.getDepartmentName());
            hasUnresolvedMapping = true;
        }

        // Intentar resolver Space
        Optional<Space> resolvedSpace = spaceResolver.resolveSpace(glpiEvent.getSpaceName());
        if (resolvedSpace.isPresent()) {
            event.setSpace(resolvedSpace.get());
        } else {
            createPendingMapping("space", glpiEvent.getSpaceName());
            hasUnresolvedMapping = true;
        }

        // Intentar resolver EventType
        Optional<EventType> resolvedEventType = eventTypeResolver.resolveEventType(glpiEvent.getEventTypeName());
        if (resolvedEventType.isPresent()) {
            event.setEventType(resolvedEventType.get());
        } else {
            createPendingMapping("event_type", glpiEvent.getEventTypeName());
            hasUnresolvedMapping = true;
        }

        // Marcar el evento como "incompleto" si no se resolvieron todos los catálogos
        if (hasUnresolvedMapping) {
            event.setState("incompleto");
        } else {
            event.setState("pendiente"); // Asignar el estado inicial normal
        }

        return eventRepository.save(event);
    }

    /**
     * Crea un registro de mapeo pendiente para ser revisado por un administrador.
     * @param type El tipo de catálogo (department, space, event_type).
     * @param originalValue El valor original del texto de GLPI que no pudo ser resuelto.
     */
    private void createPendingMapping(String type, String originalValue) {
        // Evitar duplicados de mapeos pendientes
        if (pendingMappingRepository.findByOriginalValue(originalValue).isEmpty()) {
            PendingMapping pendingMapping = new PendingMapping();
            pendingMapping.setType(type);
            pendingMapping.setOriginalValue(originalValue);
            pendingMapping.setResolved(false);
            pendingMapping.setCreatedAt(LocalDateTime.now());
            pendingMappingRepository.save(pendingMapping);
        }
    }
}
