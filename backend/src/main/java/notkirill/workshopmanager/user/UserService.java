package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.User;
import notkirill.workshopmanager.models.dto.UserRequest;
import notkirill.workshopmanager.models.dto.UserResponse;
import notkirill.workshopmanager.models.dto.mappers.UserMapper;
import notkirill.workshopmanager.models.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Autowired
    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<UserResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public UserResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(UserRequest request) {
        User entity = mapper.requestToEntity(request);
        User saved = repository.save(entity);
    }

    public void update(Long id, UserRequest request) {
        User entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);
        User saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
