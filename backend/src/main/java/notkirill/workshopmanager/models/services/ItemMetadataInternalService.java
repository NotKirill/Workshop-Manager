package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.ItemMetadataInternal;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.ItemMetadataInternalRequest;
import notkirill.workshopmanager.models.dto.ItemMetadataInternalResponse;
import notkirill.workshopmanager.models.dto.mappers.ItemMetadataInternalMapper;
import notkirill.workshopmanager.models.repositories.ItemMetadataInternalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemMetadataInternalService {
    private final ItemMetadataInternalRepository repository;
    private final ItemMetadataInternalMapper mapper;

    @Autowired
    public ItemMetadataInternalService(ItemMetadataInternalRepository repository, ItemMetadataInternalMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ItemMetadataInternalResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public ItemMetadataInternalResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(ItemMetadataInternalRequest request) {
        ItemMetadataInternal entity = mapper.requestToEntity(request);
        ItemMetadataInternal saved = repository.save(entity);
    }

    public void update(Long id, ItemMetadataInternalRequest request) {
        ItemMetadataInternal entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        ItemMetadataInternal saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
