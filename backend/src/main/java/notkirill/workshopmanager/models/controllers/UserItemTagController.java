package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.UserItemTagRequest;
import notkirill.workshopmanager.models.dto.UserItemTagResponse;
import notkirill.workshopmanager.models.idClasses.UserItemTagComplexKey;
import notkirill.workshopmanager.models.services.UserItemTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-item-tags")
public class UserItemTagController {
    private final UserItemTagService service;

    @Autowired
    public UserItemTagController(UserItemTagService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserItemTagResponse> getAll() {
        return service.getAll();
    }

    @GetMapping
    public UserItemTagResponse getById(@RequestParam Long tagId,
                                       @RequestParam Long itemPersonalMetadataId) {
        UserItemTagComplexKey id = new UserItemTagComplexKey(tagId, itemPersonalMetadataId);
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody UserItemTagRequest request) {
        service.create(request);
    }

    @PutMapping
    public void update(@RequestParam Long tagId,
                       @RequestParam Long itemPersonalMetadataId,
                       @Valid @RequestBody UserItemTagRequest request) {
        UserItemTagComplexKey id = new UserItemTagComplexKey(tagId, itemPersonalMetadataId);
        service.update(id, request);
    }

    @DeleteMapping
    public void delete(@RequestParam Long tagId,
                       @RequestParam Long itemPersonalMetadataId) {
        UserItemTagComplexKey id = new UserItemTagComplexKey(tagId, itemPersonalMetadataId);
        service.delete(id);
    }
}
