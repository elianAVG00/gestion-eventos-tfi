package ar.unla.gestion_eventos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateEventNoteRequest {
    @NotBlank
    @Size(max = 5000)
    private String content;

    private String visibility;
}
