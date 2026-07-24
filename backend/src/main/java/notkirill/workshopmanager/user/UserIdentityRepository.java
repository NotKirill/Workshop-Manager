package notkirill.workshopmanager.user;

import notkirill.workshopmanager.security.auth.AuthSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserIdentityRepository extends JpaRepository<UserIdentity, UserIdentity.CompositeId> {
    Optional<UserIdentity> findByUserId(long userId);

    Optional<UserIdentity> findByProviderAndProviderKey(AuthSource provider, String providerKey);
}