package ar.unla.gestion_eventos.Repository;

import ar.unla.gestion_eventos.Domain.SyncCursor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncCursorRepository extends JpaRepository<SyncCursor, Long> {}