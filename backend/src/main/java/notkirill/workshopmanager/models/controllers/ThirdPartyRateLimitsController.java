package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitsRequest;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitsResponse;
import notkirill.workshopmanager.models.services.ThirdPartyRateLimitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/third-party-rate-limits")
public class ThirdPartyRateLimitsController {
    private final ThirdPartyRateLimitsService service;

    @Autowired
    public ThirdPartyRateLimitsController(ThirdPartyRateLimitsService service) {
        this.service = service;
    }

    @GetMapping
    public List<ThirdPartyRateLimitsResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ThirdPartyRateLimitsResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody ThirdPartyRateLimitsRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id,
                       @Valid @RequestBody ThirdPartyRateLimitsRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
