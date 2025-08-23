package ar.unla.gestion_eventos.Repository;

import ar.unla.gestion_eventos.Domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByGlpiTicketId(String glpiTicketId);
}
