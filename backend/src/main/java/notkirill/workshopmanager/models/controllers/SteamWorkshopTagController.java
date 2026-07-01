package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.SteamWorkshopTagRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopTagResponse;
import notkirill.workshopmanager.models.services.SteamWorkshopTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/steam-workshop-tags")
public class SteamWorkshopTagController {
    private final SteamWorkshopTagService service;

    @Autowired
    public SteamWorkshopTagController(SteamWorkshopTagService service) {
        this.service = service;
    }

    @GetMapping
    public List<SteamWorkshopTagResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SteamWorkshopTagResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody SteamWorkshopTagRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody SteamWorkshopTagRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
