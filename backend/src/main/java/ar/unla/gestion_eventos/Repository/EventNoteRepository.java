package ar.unla.gestion_eventos.Repository;

import ar.unla.gestion_eventos.Domain.EventNote;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventNoteRepository extends JpaRepository<EventNote, Long> {
    Optional<EventNote> findByGlpiFollowupId(Long glpiFollowupId);
    List<EventNote> findAllByGlpiTicketId(Long glpiTicketId);
}
