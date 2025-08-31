package ar.unla.gestion_eventos.dto;
import ar.unla.gestion_eventos.Domain.Event;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDto {
    private Long id;
    private String glpiTicketId;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public static EventDto from(Event e) {
        if (e == null) return null;
        return EventDto.builder()
                .id(e.getId())
                .glpiTicketId(e.getGlpiTicketId())
                .title(e.getTitle())
                .description(e.getDescription())
                .startDateTime(e.getStartDateTime())
                .endDateTime(e.getEndDateTime())
                .build();
    }
}