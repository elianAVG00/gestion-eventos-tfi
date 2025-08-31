package ar.unla.gestion_eventos.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * comentario local sync en glpi
 */
@Entity
@Table(name = "event_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** id del followup generado en glpi */
    @Column(name = "glpi_followup_id", unique = true)
    private Long glpiFollowupId;

    @Column(name = "glpi_ticket_id")
    private Long glpiTicketId;

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(name = "created_at")
    private Instant createdAt;

    private String author;

    private String visibility;
}
