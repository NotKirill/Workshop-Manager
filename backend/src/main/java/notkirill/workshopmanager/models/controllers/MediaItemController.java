package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.MediaItemRequest;
import notkirill.workshopmanager.models.dto.MediaItemResponse;
import notkirill.workshopmanager.models.services.MediaItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/media-items")
public class MediaItemController {
    private final MediaItemService service;

    @Autowired
    public MediaItemController(MediaItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<MediaItemResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MediaItemResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody MediaItemRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody MediaItemRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
