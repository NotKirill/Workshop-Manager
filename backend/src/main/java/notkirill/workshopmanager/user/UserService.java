package notkirill.workshopmanager.user;

import notkirill.workshopmanager.exception.NotFoundException;
import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.user.permission.PermissionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {
    private final UserRepository repository;
    private final UserIdentityRepository identityRepo;

    @Autowired
    public UserService(UserRepository repository, UserIdentityRepository identityRepo) {
        this.repository = repository;
        this.identityRepo = identityRepo;
    }

    public List<User> getAll() {
        return repository.findAll(Sort.by("id"));
    }

    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    public User require(long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found"));
    }

    public long getIdByProvider(AuthSource provider, String key) {
        return identityRepo.findByProviderAndProviderKey(provider, key)
                .map(UserIdentity::getUserId)
                .orElseThrow(NoSuchElementException::new);
    }

    public List<PermissionEnum> getPermissions(User user) {
        return Arrays.stream(user.getPermissions()).sorted().toList();
    }

    public long count() {
        return repository.count();
    }

    public User save(User user) {
        return repository.save(user);
    }

    @Transactional
    public void delete(long userId) {
        identityRepo.deleteByUserId(userId);
        repository.deleteById(userId);
    }

    public List<UserIdentity> getIdentities(long userId) {
        return identityRepo.findByUserId(userId);
    }

    public Map<Long, List<UserIdentity>> getIdentities(Collection<Long> userIds) {
        Map<Long, List<UserIdentity>> result = new HashMap<>();
        for (UserIdentity identity : identityRepo.findByUserIdIn(userIds)) {
            result.computeIfAbsent(identity.getUserId(), id -> new ArrayList<>()).add(identity);
        }
        return result;
    }

    public Optional<UserIdentity> findIdentity(long userId, AuthSource provider) {
        return identityRepo.findById(new UserIdentity.CompositeId(userId, provider));
    }

    public Optional<UserIdentity> findIdentity(AuthSource provider, String key) {
        return identityRepo.findByProviderAndProviderKey(provider, key);
    }

    public UserIdentity saveIdentity(UserIdentity identity) {
        return identityRepo.save(identity);
    }

    public void deleteIdentity(UserIdentity identity) {
        identityRepo.delete(identity);
    }
}
