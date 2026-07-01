package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.MediaLinkRequest;
import notkirill.workshopmanager.models.dto.MediaLinkResponse;
import notkirill.workshopmanager.models.services.MediaLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/media-links")
public class MediaLinkController {
    private final MediaLinkService service;

    @Autowired
    public MediaLinkController(MediaLinkService service) {
        this.service = service;
    }

    @GetMapping
    public List<MediaLinkResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MediaLinkResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody MediaLinkRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody MediaLinkRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
