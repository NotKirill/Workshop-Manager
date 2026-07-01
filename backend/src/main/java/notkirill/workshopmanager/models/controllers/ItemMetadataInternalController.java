package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.ItemMetadataInternalRequest;
import notkirill.workshopmanager.models.dto.ItemMetadataInternalResponse;
import notkirill.workshopmanager.models.services.ItemMetadataInternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item-metadata-internal")
public class ItemMetadataInternalController {
    private final ItemMetadataInternalService service;

    @Autowired
    public ItemMetadataInternalController(ItemMetadataInternalService service) {
        this.service = service;
    }

    @GetMapping
    public List<ItemMetadataInternalResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ItemMetadataInternalResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody ItemMetadataInternalRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody ItemMetadataInternalRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
