package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.UserItemRelation;
import notkirill.workshopmanager.models.dto.UserItemRelationRequest;
import notkirill.workshopmanager.models.dto.UserItemRelationResponse;
import notkirill.workshopmanager.models.enums.RelationType;
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

    @GetMapping(params = {"sourceItemPersonalMetadataId", "targetItemPersonalMetadataId", "relation"})
    public UserItemRelationResponse getById(@RequestParam Long sourceItemPersonalMetadataId,
                                            @RequestParam Long targetItemPersonalMetadataId,
                                            @RequestParam RelationType relation) {
        UserItemRelation.CompositeId id = new UserItemRelation.CompositeId(
                sourceItemPersonalMetadataId, targetItemPersonalMetadataId, relation);
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
        UserItemRelation.CompositeId id = new UserItemRelation.CompositeId(
                sourceItemPersonalMetadataId, targetItemPersonalMetadataId, relation);
        service.update(id, request);
    }

    @DeleteMapping
    public void delete(@RequestParam Long sourceItemPersonalMetadataId,
                       @RequestParam Long targetItemPersonalMetadataId,
                       @RequestParam RelationType relation) {
        UserItemRelation.CompositeId id = new UserItemRelation.CompositeId(
                sourceItemPersonalMetadataId, targetItemPersonalMetadataId, relation);
        service.delete(id);
    }
}
