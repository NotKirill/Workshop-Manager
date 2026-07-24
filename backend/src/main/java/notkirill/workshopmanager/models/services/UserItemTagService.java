package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.UserItemTag;
import notkirill.workshopmanager.models.dto.UserItemTagRequest;
import notkirill.workshopmanager.models.dto.UserItemTagResponse;
import notkirill.workshopmanager.models.dto.mappers.UserItemTagMapper;
import notkirill.workshopmanager.models.repositories.UserItemTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserItemTagService {
    private final UserItemTagRepository repository;
    private final UserItemTagMapper mapper;

    @Autowired
    public UserItemTagService(UserItemTagRepository repository, UserItemTagMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<UserItemTagResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public UserItemTagResponse getById(UserItemTag.CompositeId id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(UserItemTagRequest request) {
        UserItemTag entity = mapper.requestToEntity(request);
        UserItemTag saved = repository.save(entity);
    }

    public void update(UserItemTag.CompositeId id, UserItemTagRequest request) {
        UserItemTag entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);
        UserItemTag saved = repository.save(entity);
    }

    public void delete(UserItemTag.CompositeId id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
