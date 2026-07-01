package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.ItemContentRevisionRequest;
import notkirill.workshopmanager.models.dto.ItemContentRevisionResponse;
import notkirill.workshopmanager.models.services.ItemContentRevisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item-content-revisions")
public class ItemContentRevisionController {
    private final ItemContentRevisionService service;

    @Autowired
    public ItemContentRevisionController(ItemContentRevisionService service) {
        this.service = service;
    }

    @GetMapping
    public List<ItemContentRevisionResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ItemContentRevisionResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody ItemContentRevisionRequest request) {
        service.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody ItemContentRevisionRequest request) {
        service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
