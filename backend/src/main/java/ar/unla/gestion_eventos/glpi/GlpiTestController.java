package ar.unla.gestion_eventos.glpi;

import ar.unla.gestion_eventos.Domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/glpi")
@RequiredArgsConstructor
public class GlpiTestController {
    private final GlpiSyncService service;
    //endpoint de prueba despues irian todos en el modulo de api(?
    @PostMapping("/ingest/{id}")
    public Event ingest(@PathVariable long id) {
        return service.ingestTicket(id);
    }
}
