package ar.unla.gestion_eventos.Repository;

import ar.unla.gestion_eventos.Domain.PendingMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingMappingRepository extends JpaRepository<PendingMapping, Long> {
    Optional<PendingMapping> findByOriginalValue(String originalValue);
}
