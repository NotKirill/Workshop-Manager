package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemResponse;
import notkirill.workshopmanager.models.services.SteamWorkshopItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/steam-workshop-items")
public class SteamWorkshopItemController {
    private final SteamWorkshopItemService service;

    @Autowired
    public SteamWorkshopItemController(SteamWorkshopItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<SteamWorkshopItemResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SteamWorkshopItemResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody SteamWorkshopItemRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody SteamWorkshopItemRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
