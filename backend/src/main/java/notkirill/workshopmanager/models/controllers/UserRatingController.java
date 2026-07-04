package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.UserRatingRequest;
import notkirill.workshopmanager.models.dto.UserRatingResponse;
import notkirill.workshopmanager.models.idClasses.UserRatingComplexKey;
import notkirill.workshopmanager.models.services.UserRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-ratings")
public class UserRatingController {
    private final UserRatingService service;

    @Autowired
    public UserRatingController(UserRatingService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserRatingResponse> getAll() {
        return service.getAll();
    }

    @GetMapping(params = {"userId", "itemPersonalMetadataId"})
    public UserRatingResponse getById(@RequestParam Long userId,
                                      @RequestParam Long itemPersonalMetadataId) {
        UserRatingComplexKey id = new UserRatingComplexKey(userId, itemPersonalMetadataId);
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody UserRatingRequest request) {
        service.create(request);
    }

    @PutMapping
    public void update(@RequestParam Long userId,
                       @RequestParam Long itemPersonalMetadataId,
                       @Valid @RequestBody UserRatingRequest request) {
        UserRatingComplexKey id = new UserRatingComplexKey(userId, itemPersonalMetadataId);
        service.update(id, request);
    }

    @DeleteMapping
    public void delete(@RequestParam Long userId,
                       @RequestParam Long itemPersonalMetadataId) {
        UserRatingComplexKey id = new UserRatingComplexKey(userId, itemPersonalMetadataId);
        service.delete(id);
    }
}
