package notkirill.workshopmanager.user;

import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.user.dto.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository repository;
    private final UserIdentityRepository identityRepo;
    private final UserMapper mapper;

    @Autowired
    public UserService(UserRepository repository, UserIdentityRepository identityRepo, UserMapper mapper) {
        this.repository = repository;
        this.identityRepo = identityRepo;
        this.mapper = mapper;
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    public User getByProvider(AuthSource provider, String key) {
        UserIdentity identity = identityRepo.findByProviderAndProviderKey(provider, key)
                .orElseThrow(NoSuchElementException::new);
        return getById(identity.getUserId());
    }

    public void create(User entity) {
        repository.save(entity);
    }

    public void update(Long id, User newEntity) {
        User oldEntity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromEntity(oldEntity, newEntity);

        repository.save(oldEntity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
