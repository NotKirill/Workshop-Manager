package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.UserSharedPermission;
import notkirill.workshopmanager.models.dto.UserSharedPermissionRequest;
import notkirill.workshopmanager.models.dto.UserSharedPermissionResponse;
import notkirill.workshopmanager.models.services.UserSharedPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-shared-permissions")
public class UserSharedPermissionController {
    private final UserSharedPermissionService service;

    @Autowired
    public UserSharedPermissionController(UserSharedPermissionService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserSharedPermissionResponse> getAll() {
        return service.getAll();
    }

    @GetMapping(params = {"userProviderId", "userConsumerId", "permissionId"})
    public UserSharedPermissionResponse getById(@RequestParam Long userProviderId,
                                                @RequestParam Long userConsumerId,
                                                @RequestParam Short permissionId) {
        UserSharedPermission.CompositeId id = new UserSharedPermission.CompositeId(
                userProviderId, userConsumerId, permissionId);
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody UserSharedPermissionRequest request) {
        service.create(request);
    }

    @PutMapping
    public void update(@RequestParam Long userProviderId,
                       @RequestParam Long userConsumerId,
                       @RequestParam Short permissionId,
                       @Valid @RequestBody UserSharedPermissionRequest request) {
        UserSharedPermission.CompositeId id = new UserSharedPermission.CompositeId(
                userProviderId, userConsumerId, permissionId);
        service.update(id, request);
    }

    @DeleteMapping
    public void delete(@RequestParam Long userProviderId,
                       @RequestParam Long userConsumerId,
                       @RequestParam Short permissionId) {
        UserSharedPermission.CompositeId id = new UserSharedPermission.CompositeId(
                userProviderId, userConsumerId, permissionId);
        service.delete(id);
    }
}
