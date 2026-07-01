package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.MediaEmbedRequest;
import notkirill.workshopmanager.models.dto.MediaEmbedResponse;
import notkirill.workshopmanager.models.services.MediaEmbedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/media-embeds")
public class MediaEmbedController {
    private final MediaEmbedService service;

    @Autowired
    public MediaEmbedController(MediaEmbedService service) {
        this.service = service;
    }

    @GetMapping
    public List<MediaEmbedResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MediaEmbedResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody MediaEmbedRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody MediaEmbedRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
