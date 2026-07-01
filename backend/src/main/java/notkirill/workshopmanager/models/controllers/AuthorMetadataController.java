package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.AuthorMetadataRequest;
import notkirill.workshopmanager.models.dto.AuthorMetadataResponse;
import notkirill.workshopmanager.models.services.AuthorMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/author-metadata")
public class AuthorMetadataController {
    private final AuthorMetadataService service;

    @Autowired
    public AuthorMetadataController(AuthorMetadataService service) {
        this.service = service;
    }

    @GetMapping
    public List<AuthorMetadataResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AuthorMetadataResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody AuthorMetadataRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody AuthorMetadataRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
