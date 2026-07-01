package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.GameCompatibilityRequest;
import notkirill.workshopmanager.models.dto.GameCompatibilityResponse;
import notkirill.workshopmanager.models.idClasses.GameCompatibilityComplexKey;
import notkirill.workshopmanager.models.services.GameCompatibilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game-compatibility")
public class GameCompatibilityController {

    private final GameCompatibilityService service;

    @Autowired
    public GameCompatibilityController(GameCompatibilityService service) {
        this.service = service;
    }

    @GetMapping
    public List<GameCompatibilityResponse> getAll() {
        return service.getAll();
    }

    @GetMapping
    public GameCompatibilityResponse getById(@RequestParam Long sourceGameId,
                                             @RequestParam Long targetGameId) {
        GameCompatibilityComplexKey id = new GameCompatibilityComplexKey(sourceGameId, targetGameId);
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody GameCompatibilityRequest request) {
        service.create(request);
    }

    @PutMapping
    public void update(@RequestParam Long sourceGameId,
                       @RequestParam Long targetGameId,
                       @Valid @RequestBody GameCompatibilityRequest request) {
        GameCompatibilityComplexKey id = new GameCompatibilityComplexKey(sourceGameId, targetGameId);
        service.update(id, request);
    }

    @DeleteMapping
    public void delete(@RequestParam Long sourceGameId,
                       @RequestParam Long targetGameId) {
        GameCompatibilityComplexKey id = new GameCompatibilityComplexKey(sourceGameId, targetGameId);
        service.delete(id);
    }
}
