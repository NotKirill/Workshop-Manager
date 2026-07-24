package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitRequest;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitResponse;
import notkirill.workshopmanager.models.services.ThirdPartyRateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/third-party-rate-limits")
public class ThirdPartyRateLimitController {
    private final ThirdPartyRateLimitService service;

    @Autowired
    public ThirdPartyRateLimitController(ThirdPartyRateLimitService service) {
        this.service = service;
    }

    @GetMapping
    public List<ThirdPartyRateLimitResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ThirdPartyRateLimitResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody ThirdPartyRateLimitRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id,
                       @Valid @RequestBody ThirdPartyRateLimitRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
