package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.Permission;
import notkirill.workshopmanager.models.dto.PermissionRequest;
import notkirill.workshopmanager.models.dto.PermissionResponse;
import notkirill.workshopmanager.models.dto.mappers.PermissionMapper;
import notkirill.workshopmanager.models.repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PermissionService {
    private final PermissionRepository repository;
    private final PermissionMapper mapper;

    @Autowired
    public PermissionService(PermissionRepository repository, PermissionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<PermissionResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public PermissionResponse getById(Short id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(PermissionRequest request) {
        Permission entity = mapper.requestToEntity(request);
        Permission saved = repository.save(entity);
    }

    public void update(Short id, PermissionRequest request) {
        Permission entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);
        Permission saved = repository.save(entity);
    }

    public void delete(Short id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
