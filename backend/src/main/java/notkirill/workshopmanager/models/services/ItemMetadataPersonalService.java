package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.ItemMetadataPersonal;
import notkirill.workshopmanager.models.dto.ItemMetadataPersonalRequest;
import notkirill.workshopmanager.models.dto.ItemMetadataPersonalResponse;
import notkirill.workshopmanager.models.dto.mappers.ItemMetadataPersonalMapper;
import notkirill.workshopmanager.models.repositories.ItemMetadataPersonalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemMetadataPersonalService {
    private final ItemMetadataPersonalRepository repository;
    private final ItemMetadataPersonalMapper mapper;

    @Autowired
    public ItemMetadataPersonalService(ItemMetadataPersonalRepository repository, ItemMetadataPersonalMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ItemMetadataPersonalResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public ItemMetadataPersonalResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(ItemMetadataPersonalRequest request) {
        ItemMetadataPersonal entity = mapper.requestToEntity(request);
        ItemMetadataPersonal saved = repository.save(entity);
    }

    public void update(Long id, ItemMetadataPersonalRequest request) {
        ItemMetadataPersonal entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);
        ItemMetadataPersonal saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
