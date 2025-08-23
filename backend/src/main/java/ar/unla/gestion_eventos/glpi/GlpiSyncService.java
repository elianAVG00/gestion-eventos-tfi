package ar.unla.gestion_eventos.glpi;

import ar.unla.gestion_eventos.Domain.Event;
import ar.unla.gestion_eventos.Repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GlpiSyncService {
    private final GlpiClient client;
    private final GlpiMapper mapper;
    private final EventRepository repo;

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
}
