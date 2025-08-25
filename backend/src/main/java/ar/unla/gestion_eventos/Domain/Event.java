package ar.unla.gestion_eventos.Domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "glpi_ticket_id")
    private String glpiTicketId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String requestingDepartment;

    @NotBlank
    private String responsibleName;

    @NotBlank
    @Email
    private String responsibleEmail;

    private String responsiblePhone;

    @NotNull
    private LocalDateTime startDateTime;

    @NotNull
    private LocalDateTime endDateTime;

    @NotBlank
    private String physicalSpace;

    @NotBlank
    private String eventType;

    private Integer estimatedAttendees;

    @ElementCollection
    @CollectionTable(name = "event_equipment", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "equipment")
    private Set<String> equipmentNeeded;

    private Boolean needsEarlySetup;

    private Boolean needsTechnicalAssistance;

    private Boolean recurring;

    /** Última fecha de modificación del ticket en GLPI usada para la syncro */
    @Column(name = "glpi_date_mod")
    private Instant dateMod;
}
