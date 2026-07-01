package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.SteamWorkshopVoteDataRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopVoteDataResponse;
import notkirill.workshopmanager.models.services.SteamWorkshopVoteDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/steam-workshop-vote-data")
public class SteamWorkshopVoteDataController {
    private final SteamWorkshopVoteDataService service;

    @Autowired
    public SteamWorkshopVoteDataController(SteamWorkshopVoteDataService service) {
        this.service = service;
    }

    @GetMapping
    public List<SteamWorkshopVoteDataResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SteamWorkshopVoteDataResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody SteamWorkshopVoteDataRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody SteamWorkshopVoteDataRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
