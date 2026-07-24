package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.SteamWorkshopItemTag;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemTagRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemTagResponse;
import notkirill.workshopmanager.models.services.SteamWorkshopItemTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/steam-workshop-item-tags")
public class SteamWorkshopItemTagController {
    private final SteamWorkshopItemTagService service;

    @Autowired
    public SteamWorkshopItemTagController(SteamWorkshopItemTagService service) {
        this.service = service;
    }

    @GetMapping
    public List<SteamWorkshopItemTagResponse> getAll() {
        return service.getAll();
    }

    @GetMapping(params = {"internalId", "tagId"})
    public SteamWorkshopItemTagResponse getById(@RequestParam Long internalId,
                                                @RequestParam Long tagId) {
        SteamWorkshopItemTag.CompositeId id = new SteamWorkshopItemTag.CompositeId(internalId, tagId);
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody SteamWorkshopItemTagRequest request) {
        service.create(request);
    }

    @PutMapping
    public void update(@RequestParam Long internalId,
                       @RequestParam Long tagId,
                       @Valid @RequestBody SteamWorkshopItemTagRequest request) {
        SteamWorkshopItemTag.CompositeId id = new SteamWorkshopItemTag.CompositeId(internalId, tagId);
        service.update(id, request);
    }

    @DeleteMapping
    public void delete(@RequestParam Long internalId,
                       @RequestParam Long tagId) {
        SteamWorkshopItemTag.CompositeId id = new SteamWorkshopItemTag.CompositeId(internalId, tagId);
        service.delete(id);
    }
}
