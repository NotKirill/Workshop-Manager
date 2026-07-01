package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.ItemContentRequest;
import notkirill.workshopmanager.models.dto.ItemContentResponse;
import notkirill.workshopmanager.models.services.ItemContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item-contents")
public class ItemContentController {

    private final ItemContentService service;

    @Autowired
    public ItemContentController(ItemContentService service) {
        this.service = service;
    }

    @GetMapping
    public List<ItemContentResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ItemContentResponse getByIdOrInternalMetadataId(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody ItemContentRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody ItemContentRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

