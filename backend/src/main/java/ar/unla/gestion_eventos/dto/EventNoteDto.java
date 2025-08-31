package ar.unla.gestion_eventos.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventNoteDto {
    private Long id;
    private Long glpiFollowupId;
    private Long glpiTicketId;
    private String content;
    private Instant createdAt;
    private String author;
}