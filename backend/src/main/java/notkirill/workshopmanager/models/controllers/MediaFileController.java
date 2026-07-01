package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.MediaFileRequest;
import notkirill.workshopmanager.models.dto.MediaFileResponse;
import notkirill.workshopmanager.models.services.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/media-files")
public class MediaFileController {
    private final MediaFileService service;

    @Autowired
    public MediaFileController(MediaFileService service) {
        this.service = service;
    }

    @GetMapping
    public List<MediaFileResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MediaFileResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody MediaFileRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody MediaFileRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
