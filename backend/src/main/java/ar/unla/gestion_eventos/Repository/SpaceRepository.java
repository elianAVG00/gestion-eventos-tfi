package ar.unla.gestion_eventos.Repository;

import ar.unla.gestion_eventos.Domain.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    Optional<Space> findByName(String name);
}