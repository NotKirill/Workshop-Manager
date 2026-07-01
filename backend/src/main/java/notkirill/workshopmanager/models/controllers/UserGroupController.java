package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.UserGroupRequest;
import notkirill.workshopmanager.models.dto.UserGroupResponse;
import notkirill.workshopmanager.models.services.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-groups")
public class UserGroupController {
    private final UserGroupService service;

    @Autowired
    public UserGroupController(UserGroupService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserGroupResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UserGroupResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody UserGroupRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody UserGroupRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
