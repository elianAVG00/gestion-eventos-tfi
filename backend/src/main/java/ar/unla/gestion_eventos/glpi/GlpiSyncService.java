package ar.unla.gestion_eventos.glpi;

import ar.unla.gestion_eventos.Domain.Event;
import ar.unla.gestion_eventos.Repository.EventRepository;
import ar.unla.gestion_eventos.Repository.SyncCursorRepository;
import ar.unla.gestion_eventos.Domain.SyncCursor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GlpiSyncService {
    private final GlpiClient client;
    private final GlpiMapper mapper;
    private final EventRepository repo;
    private final SyncCursorRepository cursorRepo;
    private static final int PAGE_SIZE = 50;
    private static final int CHUNK_SIZE = 50;

    /** trae el ticket y lo devuelve, llama al EventoRepository y tambien lo guarda en la DB */
    public Event ingestTicket(long ticketId) {
        client.openSession();
        try {
            Map<String,Object> t = client.getTicket(ticketId, true, true); // expand + HTML crudo
            String html = (String) t.getOrDefault("content", "");
            var respuestaPares = FormAnswerParser.extractRespuestaFormPares(html);
            var respuestaParseada = mapper.toParsedForm(respuestaPares);
            var eventoExistente = repo.findByGlpiTicketId(String.valueOf(ticketId));
            if (eventoExistente.isPresent()) return eventoExistente.get();

            var nuevoEvento = mapper.toEvent(t, respuestaParseada);
            return repo.save(nuevoEvento);
        } finally {
            client.closeSession();
        }
    }

    public record SyncResult(int processed, int created, int updated, int failed, Instant lastDateMod, Long lastId) {}

    public SyncResult syncNow() {
        client.openSession();
        try {
            String token = client.getSessionToken();
            TicketSearchOptions so = client.getTicketSearchOptions(token);
            SyncCursor cursor = cursorRepo.findById(1L)
                    .orElse(SyncCursor.builder().id(1L).lastDateMod(Instant.now()).lastId(0L).build());
            Instant lastDateMod = cursor.getLastDateMod() != null ? cursor.getLastDateMod() : Instant.now();
            long lastId = cursor.getLastId() != null ? cursor.getLastId() : 0L;

            int processed = 0, created = 0, updated = 0, failed = 0;
            Instant newLastDateMod = lastDateMod;
            long newLastId = lastId;

            int offset = 0;
            boolean hasMore = true;
            while (hasMore) {
                GlpiClient.SearchResponse sr = client.searchTickets(token, so, lastDateMod, lastId, offset, offset + PAGE_SIZE - 1);
                List<Map<String, Object>> refs = sr.items();
                if (refs.isEmpty()) break;
                List<Long> ids = new ArrayList<>();
                for (Map<String, Object> r : refs) {
                    Object idObj = r.get("2");// el 2 representa a los ids segun las ticket search oiptions
                    if (idObj instanceof Number n) ids.add(n.longValue());
                }
                for (int i = 0; i < ids.size(); i += CHUNK_SIZE) {
                    List<Long> chunk = ids.subList(i, Math.min(ids.size(), i + CHUNK_SIZE));
                    List<Map<String, Object>> tickets = client.getMultipleTickets(token, chunk, true);
                    for (Map<String, Object> t : tickets) {
                        processed++;
                        try {
                            String html = String.valueOf(t.getOrDefault("content", ""));
                            var qa = FormAnswerParser.extractRespuestaFormPares(html);
                            var pf = mapper.toParsedForm(qa);
                            Event mapped = mapper.toEvent(t, pf);
                            var existing = repo.findByGlpiTicketId(mapped.getGlpiTicketId());
                            if (existing.isPresent()) {
                                updateEvent(existing.get(), mapped);
                                repo.save(existing.get());
                                updated++;
                            } else {
                                repo.save(mapped);
                                created++;
                            }
                            Instant dm = mapped.getDateMod();
                            long tid = Long.parseLong(mapped.getGlpiTicketId());
                            if (dm != null && (dm.isAfter(newLastDateMod) ||
                                    (dm.equals(newLastDateMod) && tid > newLastId))) {
                                newLastDateMod = dm;
                                newLastId = tid;
                            }
                        } catch (Exception e) {
                            failed++;
                        }
                    }
                }
                offset += PAGE_SIZE;
                hasMore = sr.hasMore();
            }

            cursor.setLastDateMod(newLastDateMod);
            cursor.setLastId(newLastId);
            cursor.setUpdatedAt(Instant.now());
            cursorRepo.save(cursor);
            return new SyncResult(processed, created, updated, failed, newLastDateMod, newLastId);
        } finally {
            client.closeSession();
        }
    }

    private static void updateEvent(Event target, Event src) {
        target.setTitle(src.getTitle());
        target.setDescription(src.getDescription());
        target.setRequestingDepartment(src.getRequestingDepartment());
        target.setResponsibleName(src.getResponsibleName());
        target.setResponsibleEmail(src.getResponsibleEmail());
        target.setResponsiblePhone(src.getResponsiblePhone());
        target.setStartDateTime(src.getStartDateTime());
        target.setEndDateTime(src.getEndDateTime());
        target.setPhysicalSpace(src.getPhysicalSpace());
        target.setEventType(src.getEventType());
        target.setEstimatedAttendees(src.getEstimatedAttendees());
        target.setEquipmentNeeded(src.getEquipmentNeeded());
        target.setNeedsEarlySetup(src.getNeedsEarlySetup());
        target.setNeedsTechnicalAssistance(src.getNeedsTechnicalAssistance());
        target.setRecurring(src.getRecurring());
        target.setDateMod(src.getDateMod());
    }
}
