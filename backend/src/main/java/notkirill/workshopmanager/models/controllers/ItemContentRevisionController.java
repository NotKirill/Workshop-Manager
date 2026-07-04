package notkirill.workshopmanager.models.controllers;

import jakarta.validation.Valid;
import notkirill.workshopmanager.models.dto.ItemContentRevisionRequest;
import notkirill.workshopmanager.models.dto.ItemContentRevisionResponse;
import notkirill.workshopmanager.models.idClasses.ItemContentRevisionComplexKey;
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

    @GetMapping(params = {"itemContentId", "revision"})
    public ItemContentRevisionResponse getById(@RequestParam Long itemContentId,
                                               @RequestParam Long revision) {
        ItemContentRevisionComplexKey id = new ItemContentRevisionComplexKey(itemContentId, revision);
        return service.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody ItemContentRevisionRequest request) {
        service.create(request);
    }

    @PutMapping
    public void update(@RequestParam Long itemContentId,
                       @RequestParam Long revision,
                       @Valid @RequestBody ItemContentRevisionRequest request) {
        ItemContentRevisionComplexKey id = new ItemContentRevisionComplexKey(itemContentId, revision);
        service.update(id, request);
    }

    @DeleteMapping
    public void delete(@RequestParam Long itemContentId,
                       @RequestParam Long revision) {
        ItemContentRevisionComplexKey id = new ItemContentRevisionComplexKey(itemContentId, revision);
        service.delete(id);
    }
}
