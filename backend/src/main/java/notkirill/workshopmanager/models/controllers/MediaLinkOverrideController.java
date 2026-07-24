package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.MediaLinkOverride;
import notkirill.workshopmanager.models.dto.MediaLinkOverrideRequest;
import notkirill.workshopmanager.models.dto.MediaLinkOverrideResponse;
import notkirill.workshopmanager.models.services.MediaLinkOverrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/media-link-overrides")
public class MediaLinkOverrideController {
    private final MediaLinkOverrideService service;

    @Autowired
    public MediaLinkOverrideController(MediaLinkOverrideService service) {
        this.service = service;
    }

    @GetMapping
    public List<MediaLinkOverrideResponse> getAll() {
        return service.getAll();
    }

    @GetMapping(params = {"linkId", "userId"})
    public MediaLinkOverrideResponse getById(@RequestParam Long linkId,
                                             @RequestParam Long userId) {
        MediaLinkOverride.CompositeId id = new MediaLinkOverride.CompositeId(linkId, userId);
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody MediaLinkOverrideRequest request) {
        service.create(request);
    }

    @PutMapping
    public void update(@RequestParam Long linkId,
                       @RequestParam Long userId,
                       @Valid @RequestBody MediaLinkOverrideRequest request) {
        MediaLinkOverride.CompositeId id = new MediaLinkOverride.CompositeId(linkId, userId);
        service.update(id, request);
    }

    @DeleteMapping
    public void delete(@RequestParam Long linkId,
                       @RequestParam Long userId) {
        MediaLinkOverride.CompositeId id = new MediaLinkOverride.CompositeId(linkId, userId);
        service.delete(id);
    }
}
