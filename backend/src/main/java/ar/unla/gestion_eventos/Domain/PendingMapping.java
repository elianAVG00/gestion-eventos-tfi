package ar.unla.gestion_eventos.Domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "pending_mapping")
@Getter
@Setter
@NoArgsConstructor
public class PendingMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tipo de mapeo: "department", "space", "event_type"
    private String type;

    // El valor original del texto que no se pudo mapear
    private String originalValue;

    // Indica si un administrador ya resolvió el mapeo
    private Boolean isResolved = false;

    private LocalDateTime createdAt;
}
