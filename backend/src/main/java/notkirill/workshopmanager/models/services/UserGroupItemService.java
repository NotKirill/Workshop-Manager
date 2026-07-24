package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.UserGroupItem;
import notkirill.workshopmanager.models.dto.UserGroupItemRequest;
import notkirill.workshopmanager.models.dto.UserGroupItemResponse;
import notkirill.workshopmanager.models.dto.mappers.UserGroupItemMapper;
import notkirill.workshopmanager.models.repositories.UserGroupItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserGroupItemService {
    private final UserGroupItemRepository repository;
    private final UserGroupItemMapper mapper;

    @Autowired
    public UserGroupItemService(UserGroupItemRepository repository, UserGroupItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<UserGroupItemResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public UserGroupItemResponse getById(UserGroupItem.CompositeId id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(UserGroupItemRequest request) {
        UserGroupItem entity = mapper.requestToEntity(request);
        UserGroupItem saved = repository.save(entity);
    }

    public void update(UserGroupItem.CompositeId id, UserGroupItemRequest request) {
        UserGroupItem entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);
        UserGroupItem saved = repository.save(entity);
    }

    public void delete(UserGroupItem.CompositeId id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
