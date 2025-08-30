package ar.unla.gestion_eventos.Controller;

import ar.unla.gestion_eventos.Domain.Space;
import ar.unla.gestion_eventos.Service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/spaces")
public class SpaceController {

    private final SpaceService spaceService;

    @Autowired
    public SpaceController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    // CREATE - POST /admin/spaces
    @PostMapping
    public ResponseEntity<Space> createSpace(@RequestBody Space space) {
        Space newSpace = spaceService.createSpace(space);
        return ResponseEntity.created(URI.create("/admin/spaces/" + newSpace.getId())).body(newSpace);
    }

    // READ ALL - GET /admin/spaces
    @GetMapping
    public List<Space> getAllSpaces() {
        return spaceService.findAllSpaces();
    }

    // READ ONE - GET /admin/spaces/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Space> getSpaceById(@PathVariable Long id) {
        Optional<Space> space = spaceService.findSpaceById(id);
        return space.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // UPDATE - PUT /admin/spaces/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Space> updateSpace(@PathVariable Long id, @RequestBody Space updatedSpace) {
        Optional<Space> space = spaceService.updateSpace(id, updatedSpace);
        return space.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE - DELETE /admin/spaces/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpace(@PathVariable Long id) {
        if (spaceService.findSpaceById(id).isPresent()) {
            spaceService.deleteSpace(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}