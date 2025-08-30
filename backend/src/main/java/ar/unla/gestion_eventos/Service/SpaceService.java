package ar.unla.gestion_eventos.Service;

import ar.unla.gestion_eventos.Domain.Space;
import ar.unla.gestion_eventos.Repository.SpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SpaceService {

    private final SpaceRepository spaceRepository;

    @Autowired
    public SpaceService(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    public Space createSpace(Space space) {
        return spaceRepository.save(space);
    }

    public List<Space> findAllSpaces() {
        return spaceRepository.findAll();
    }

    public Optional<Space> findSpaceById(Long id) {
        return spaceRepository.findById(id);
    }

    public Optional<Space> updateSpace(Long id, Space updatedSpace) {
        return spaceRepository.findById(id).map(space -> {
            space.setName(updatedSpace.getName());
            space.setCapacity(updatedSpace.getCapacity());
            space.setActive(updatedSpace.isActive());
            return spaceRepository.save(space);
        });
    }

    public void deleteSpace(Long id) {
        spaceRepository.deleteById(id);
    }
}