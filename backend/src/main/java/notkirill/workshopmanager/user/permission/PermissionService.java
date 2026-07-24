package notkirill.workshopmanager.user.permission;

import notkirill.workshopmanager.user.permission.dto.PermissionMapper;
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

    public List<Permission> getAll() {
        return repository.findAll();
    }

    public List<Permission> getAllByIds(short[] ids) {
        return repository.findAllByIds(ids);
    }

    public Permission getById(Short id) {
        return repository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    public Permission getByName(String name) {
        return repository.findByName(name)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(Permission entity) {
        repository.save(entity);
    }

    public void update(Short id, Permission newEntity) {
        Permission oldEntity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromEntity(oldEntity, newEntity);

        repository.save(oldEntity);
    }

    public void delete(Short id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
