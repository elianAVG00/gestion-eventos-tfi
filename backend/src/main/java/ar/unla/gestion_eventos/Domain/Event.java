package ar.unla.gestion_eventos.Domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(name = "glpi_ticket_id", unique = true)
    private String glpiTicketId;

    @NotBlank
    private String title;
    private String state;

    @NotBlank
    private String description;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

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

    @ManyToOne
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @ManyToOne
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType eventType;

    private Integer estimatedAttendees;

    @ElementCollection
    @CollectionTable(name = "event_equipment", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "equipment")
    private Set<String> equipmentNeeded;
    private Boolean needsEarlySetup;
    private Boolean needsTechnicalAssistance;
    private Boolean recurring;
    private Integer setupTime; // En minutos
    private Integer teardownTime; // En minutos

    @Column(name = "glpi_date_mod")
    private Instant dateMod;
}