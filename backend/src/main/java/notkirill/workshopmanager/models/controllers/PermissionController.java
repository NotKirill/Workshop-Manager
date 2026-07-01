package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.PermissionRequest;
import notkirill.workshopmanager.models.dto.PermissionResponse;
import notkirill.workshopmanager.models.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    private final PermissionService service;

    @Autowired
    public PermissionController(PermissionService service) {
        this.service = service;
    }

    @GetMapping
    public List<PermissionResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PermissionResponse getById(@PathVariable Short id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody PermissionRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Short id,
                       @Valid @RequestBody PermissionRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Short id) {
        service.delete(id);
    }
}
