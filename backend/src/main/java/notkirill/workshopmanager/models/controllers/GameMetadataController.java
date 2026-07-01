package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.GameMetadataRequest;
import notkirill.workshopmanager.models.dto.GameMetadataResponse;
import notkirill.workshopmanager.models.services.GameMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game-metadata")
public class GameMetadataController {

    private final GameMetadataService service;

    @Autowired
    public GameMetadataController(GameMetadataService service) {
        this.service = service;
    }

    @GetMapping
    public List<GameMetadataResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public GameMetadataResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody GameMetadataRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody GameMetadataRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}