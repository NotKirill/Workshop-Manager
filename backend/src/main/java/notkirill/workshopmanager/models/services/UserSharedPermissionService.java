package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.UserSharedPermission;
import notkirill.workshopmanager.models.dto.UserSharedPermissionRequest;
import notkirill.workshopmanager.models.dto.UserSharedPermissionResponse;
import notkirill.workshopmanager.models.dto.mappers.UserSharedPermissionMapper;
import notkirill.workshopmanager.models.idClasses.UserSharedPermissionComplexKey;
import notkirill.workshopmanager.models.repositories.UserSharedPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserSharedPermissionService {
    private final UserSharedPermissionRepository repository;
    private final UserSharedPermissionMapper mapper;

    @Autowired
    public UserSharedPermissionService(UserSharedPermissionRepository repository, UserSharedPermissionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<UserSharedPermissionResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public UserSharedPermissionResponse getById(UserSharedPermissionComplexKey id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(UserSharedPermissionRequest request) {
        UserSharedPermission entity = mapper.requestToEntity(request);
        UserSharedPermission saved = repository.save(entity);
    }

    public void update(UserSharedPermissionComplexKey id, UserSharedPermissionRequest request) {
        UserSharedPermission entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);
        UserSharedPermission saved = repository.save(entity);
    }

    public void delete(UserSharedPermissionComplexKey id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
