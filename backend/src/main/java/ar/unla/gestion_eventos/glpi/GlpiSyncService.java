package ar.unla.gestion_eventos.glpi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GlpiSyncService {
    private final GlpiClient client;
    private final GlpiMapper mapper;
//    private final EventRepository repo;

    /** trae el ticket y lo devuelve (hay q modificar para q llame al EventoRepository y tambien lo guarde en la DB */
    public GlpiMapper.ParsedForm ingestTicket(long ticketId) {
        client.openSession();
        try {
            Map<String,Object> t = client.getTicket(ticketId, true, true); // expand + HTML crudo
            String html = (String) t.getOrDefault("content", "");
            var respuesta = FormAnswerParser.extractRespuestaFormPares(html);
            var respuestaFormateada = mapper.toParsedForm(respuesta);

            return respuestaFormateada;
        } finally {
            client.closeSession();
        }
    }
}
