package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.UserTag;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.UserTagRequest;
import notkirill.workshopmanager.models.dto.UserTagResponse;
import notkirill.workshopmanager.models.dto.mappers.UserTagMapper;
import notkirill.workshopmanager.models.repositories.UserTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTagService {
    private final UserTagRepository repository;
    private final UserTagMapper mapper;

    @Autowired
    public UserTagService(UserTagRepository repository, UserTagMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<UserTagResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public UserTagResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(UserTagRequest request) {
        UserTag entity = mapper.requestToEntity(request);
        UserTag saved = repository.save(entity);
    }

    public void update(Long id, UserTagRequest request) {
        UserTag entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        UserTag saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
