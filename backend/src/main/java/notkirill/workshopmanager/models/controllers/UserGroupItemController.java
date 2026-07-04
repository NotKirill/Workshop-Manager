package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.UserGroupItemRequest;
import notkirill.workshopmanager.models.dto.UserGroupItemResponse;
import notkirill.workshopmanager.models.idClasses.UserGroupItemComplexKey;
import notkirill.workshopmanager.models.services.UserGroupItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-group-items")
public class UserGroupItemController {
    private final UserGroupItemService service;

    @Autowired
    public UserGroupItemController(UserGroupItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserGroupItemResponse> getAll() {
        return service.getAll();
    }

    @GetMapping(params = {"userGroupId", "itemPersonalMetadataId"})
    public UserGroupItemResponse getById(@RequestParam Long userGroupId,
                                         @RequestParam Long itemPersonalMetadataId) {
        UserGroupItemComplexKey id = new UserGroupItemComplexKey(userGroupId, itemPersonalMetadataId);
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody UserGroupItemRequest request) {
        service.create(request);
    }

    @PutMapping
    public void update(@RequestParam Long userGroupId,
                       @RequestParam Long itemPersonalMetadataId,
                       @Valid @RequestBody UserGroupItemRequest request) {
        UserGroupItemComplexKey id = new UserGroupItemComplexKey(userGroupId, itemPersonalMetadataId);
        service.update(id, request);
    }

    @DeleteMapping
    public void delete(@RequestParam Long userGroupId,
                       @RequestParam Long itemPersonalMetadataId) {
        UserGroupItemComplexKey id = new UserGroupItemComplexKey(userGroupId, itemPersonalMetadataId);
        service.delete(id);
    }
}
