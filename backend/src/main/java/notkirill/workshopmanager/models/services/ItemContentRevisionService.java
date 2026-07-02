package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.ItemContentRevision;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.ItemContentRevisionRequest;
import notkirill.workshopmanager.models.dto.ItemContentRevisionResponse;
import notkirill.workshopmanager.models.dto.mappers.ItemContentRevisionMapper;
import notkirill.workshopmanager.models.idClasses.ItemContentRevisionComplexKey;
import notkirill.workshopmanager.models.repositories.ItemContentRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemContentRevisionService {
    private final ItemContentRevisionRepository repository;
    private final ItemContentRevisionMapper mapper;

    @Autowired
    public ItemContentRevisionService(ItemContentRevisionRepository repository, ItemContentRevisionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ItemContentRevisionResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public ItemContentRevisionResponse getById(ItemContentRevisionComplexKey id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(ItemContentRevisionRequest request) {
        ItemContentRevision entity = mapper.requestToEntity(request);
        ItemContentRevision saved = repository.save(entity);
    }

    public void update(ItemContentRevisionComplexKey id, ItemContentRevisionRequest request) {
        ItemContentRevision entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        ItemContentRevision saved = repository.save(entity);
    }

    public void delete(ItemContentRevisionComplexKey id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
