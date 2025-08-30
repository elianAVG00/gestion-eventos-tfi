package ar.unla.gestion_eventos.Dto;

import lombok.Data;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class GlpiEventDto {

    private String title;
    private String ticketId;
    private String description;
    private String responsibleName;
    private String responsibleEmail;
    private String responsiblePhone;
    private String departmentName;
    private String spaceName;
    private String eventTypeName;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer estimatedAttendees;
    private Set<String> equipmentNeeded;
    private Boolean needsEarlySetup;
    private Boolean needsTechnicalAssistance;
    private Boolean recurring;
    private Instant dateMod;
}
