package ar.unla.gestion_eventos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateEventNoteResponse {
    private EventNoteDto eventNote;
    private EventDto event;
    private boolean refreshPending;
}
