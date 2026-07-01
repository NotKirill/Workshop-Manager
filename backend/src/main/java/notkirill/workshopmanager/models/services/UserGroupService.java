package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.UserGroup;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.UserGroupRequest;
import notkirill.workshopmanager.models.dto.UserGroupResponse;
import notkirill.workshopmanager.models.dto.mappers.UserGroupMapper;
import notkirill.workshopmanager.models.repositories.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserGroupService {
    private final UserGroupRepository repository;
    private final UserGroupMapper mapper;

    @Autowired
    public UserGroupService(UserGroupRepository repository, UserGroupMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<UserGroupResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public UserGroupResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(UserGroupRequest request) {
        UserGroup entity = mapper.requestToEntity(request);
        UserGroup saved = repository.save(entity);
    }

    public void update(Long id, UserGroupRequest request) {
        UserGroup entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        UserGroup saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
