package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.SteamWorkshopExtendedRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopExtendedResponse;
import notkirill.workshopmanager.models.services.SteamWorkshopExtendedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/steam-workshop-extended")
public class SteamWorkshopExtendedController {
    private final SteamWorkshopExtendedService service;

    @Autowired
    public SteamWorkshopExtendedController(SteamWorkshopExtendedService service) {
        this.service = service;
    }

    @GetMapping
    public List<SteamWorkshopExtendedResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SteamWorkshopExtendedResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody SteamWorkshopExtendedRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody SteamWorkshopExtendedRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
