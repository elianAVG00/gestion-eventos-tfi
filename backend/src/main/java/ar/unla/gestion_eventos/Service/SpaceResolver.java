package ar.unla.gestion_eventos.Service;

import ar.unla.gestion_eventos.Domain.Space;
import ar.unla.gestion_eventos.Repository.SpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Optional;

@Service
public class SpaceResolver {

    private final SpaceRepository spaceRepository;

    @Autowired
    public SpaceResolver(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    public Optional<Space> resolveSpace(String glpiName) {
        if (glpiName == null || glpiName.trim().isEmpty()) {
            return Optional.empty();
        }

        String normalizedName = normalizeString(glpiName);

        return spaceRepository.findByName(normalizedName);
    }

    private String normalizeString(String text) {
        String result = text.toLowerCase().trim();

        result = Normalizer.normalize(result, Normalizer.Form.NFD);
        result = result.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        result = result.replaceAll("\\s+", " ");
        return result;
    }
}