package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.ItemContent;
import notkirill.workshopmanager.models.dto.ItemContentRequest;
import notkirill.workshopmanager.models.dto.ItemContentResponse;
import notkirill.workshopmanager.models.dto.mappers.ItemContentMapper;
import notkirill.workshopmanager.models.repositories.ItemContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemContentService {

    private final ItemContentRepository repository;
    private final ItemContentMapper mapper;

    @Autowired
    public ItemContentService(ItemContentRepository repository, ItemContentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ItemContentResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public ItemContentResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(ItemContentRequest request) {
        ItemContent entity = mapper.requestToEntity(request);
        ItemContent saved = repository.save(entity);
    }

    public void update(Long id,
                       ItemContentRequest request) {
        ItemContent entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);

        ItemContent saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}

