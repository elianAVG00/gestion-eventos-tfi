package ar.unla.gestion_eventos.glpi;

import ar.unla.gestion_eventos.Domain.Event;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/admin/glpi")
@RequiredArgsConstructor
public class GlpiTestController {
    private final GlpiSyncService service;
    //endpoint de prueba despues irian todos en el modulo de api(?
    @PostMapping("/ingest/{id}")
        public Event ingest ( @PathVariable long id) {
        try {
            if (id <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El id del ticket que busca, debe ser mayor que 0"
                );
            }
            return service.ingestTicket(id);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error procesando ticket GLPI", e
            );
        }
    }

}
