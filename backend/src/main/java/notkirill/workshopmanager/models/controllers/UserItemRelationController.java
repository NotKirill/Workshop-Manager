package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.UserItemRelationRequest;
import notkirill.workshopmanager.models.dto.UserItemRelationResponse;
import notkirill.workshopmanager.models.enums.RelationType;
import notkirill.workshopmanager.models.idClasses.UserItemRelationComplexKey;
import notkirill.workshopmanager.models.services.UserItemRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-item-relations")
public class UserItemRelationController {
    private final UserItemRelationService service;

    @Autowired
    public UserItemRelationController(UserItemRelationService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserItemRelationResponse> getAll() {
        return service.getAll();
    }

    @GetMapping
    public UserItemRelationResponse getById(@RequestParam Long sourceItemPersonalMetadataId,
                                            @RequestParam Long targetItemPersonalMetadataId,
                                            @RequestParam RelationType relation) {
        UserItemRelationComplexKey id = new UserItemRelationComplexKey(sourceItemPersonalMetadataId, targetItemPersonalMetadataId, relation);
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody UserItemRelationRequest request) {
        service.create(request);
    }

    @PutMapping
    public void update(@RequestParam Long sourceItemPersonalMetadataId,
                       @RequestParam Long targetItemPersonalMetadataId,
                       @RequestParam RelationType relation,
                       @Valid @RequestBody UserItemRelationRequest request) {
        UserItemRelationComplexKey id = new UserItemRelationComplexKey(sourceItemPersonalMetadataId, targetItemPersonalMetadataId, relation);
        service.update(id, request);
    }

    @DeleteMapping
    public void delete(@RequestParam Long sourceItemPersonalMetadataId,
                       @RequestParam Long targetItemPersonalMetadataId,
                       @RequestParam RelationType relation) {
        UserItemRelationComplexKey id = new UserItemRelationComplexKey(sourceItemPersonalMetadataId, targetItemPersonalMetadataId, relation);
        service.delete(id);
    }
}
