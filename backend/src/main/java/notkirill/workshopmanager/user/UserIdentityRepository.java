package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserIdentityRepository extends JpaRepository<UserIdentity, UserIdentity.CompositeId> {
    Optional<UserIdentity> findByUserId(long userId);
}