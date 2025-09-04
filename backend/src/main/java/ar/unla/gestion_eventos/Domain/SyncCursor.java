package ar.unla.gestion_eventos.Domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sync_cursor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncCursor {
    @Id
    private Long id;
    private Instant lastDateMod;
    private Long lastId;
    private Instant updatedAt;
}