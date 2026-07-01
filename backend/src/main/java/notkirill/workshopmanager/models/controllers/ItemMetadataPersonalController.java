package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.ItemMetadataPersonalRequest;
import notkirill.workshopmanager.models.dto.ItemMetadataPersonalResponse;
import notkirill.workshopmanager.models.services.ItemMetadataPersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item-metadata-personal")
public class ItemMetadataPersonalController {
    private final ItemMetadataPersonalService service;

    @Autowired
    public ItemMetadataPersonalController(ItemMetadataPersonalService service) {
        this.service = service;
    }

    @GetMapping
    public List<ItemMetadataPersonalResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ItemMetadataPersonalResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody ItemMetadataPersonalRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody ItemMetadataPersonalRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
