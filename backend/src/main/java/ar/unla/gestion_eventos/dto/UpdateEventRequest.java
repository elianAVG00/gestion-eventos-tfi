package ar.unla.gestion_eventos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateEventRequest {
    private String title;        // GLPI.name
    private String description;  // GLPI.content
    private Integer statusId;    // GLPI.status
    private Integer priorityId;  // GLPI.priority
}