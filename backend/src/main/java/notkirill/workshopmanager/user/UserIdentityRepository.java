package notkirill.workshopmanager.user;

import notkirill.workshopmanager.security.auth.AuthSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserIdentityRepository extends JpaRepository<UserIdentity, UserIdentity.CompositeId> {
    List<UserIdentity> findByUserId(long userId);

    List<UserIdentity> findByUserIdIn(Collection<Long> userIds);

    Optional<UserIdentity> findByProviderAndProviderKey(AuthSource provider, String providerKey);

    long deleteByUserId(long userId);
}
