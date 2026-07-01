package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.UserItemTag;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.UserItemTagRequest;
import notkirill.workshopmanager.models.dto.UserItemTagResponse;
import notkirill.workshopmanager.models.dto.mappers.UserItemTagMapper;
import notkirill.workshopmanager.models.idClasses.UserItemTagComplexKey;
import notkirill.workshopmanager.models.repositories.UserItemTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public UserItemTagResponse getById(UserItemTagComplexKey id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(UserItemTagRequest request) {
        UserItemTag entity = mapper.requestToEntity(request);
        UserItemTag saved = repository.save(entity);
    }

    public void update(UserItemTagComplexKey id, UserItemTagRequest request) {
        UserItemTag entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        UserItemTag saved = repository.save(entity);
    }

    public void delete(UserItemTagComplexKey id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
