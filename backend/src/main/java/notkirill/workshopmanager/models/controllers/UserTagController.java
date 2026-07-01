package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.UserTagRequest;
import notkirill.workshopmanager.models.dto.UserTagResponse;
import notkirill.workshopmanager.models.services.UserTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-tags")
public class UserTagController {
    private final UserTagService service;

    @Autowired
    public UserTagController(UserTagService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserTagResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UserTagResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody UserTagRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody UserTagRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
