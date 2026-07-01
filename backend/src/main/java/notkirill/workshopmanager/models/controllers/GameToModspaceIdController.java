package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.GameToModspaceIdRequest;
import notkirill.workshopmanager.models.dto.GameToModspaceIdResponse;
import notkirill.workshopmanager.models.services.GameToModspaceIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game-to-modspace-ids")
public class GameToModspaceIdController {
    private final GameToModspaceIdService service;

    @Autowired
    public GameToModspaceIdController(GameToModspaceIdService service) {
        this.service = service;
    }

    @GetMapping
    public List<GameToModspaceIdResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public GameToModspaceIdResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody GameToModspaceIdRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody GameToModspaceIdRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
