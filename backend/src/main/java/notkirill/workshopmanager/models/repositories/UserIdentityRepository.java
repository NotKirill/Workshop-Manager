package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserIdentity;
import notkirill.workshopmanager.models.idClasses.UserIdentityComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserIdentityRepository extends JpaRepository<UserIdentity, UserIdentityComplexKey> {
    Optional<UserIdentity> findByUserId(long userId);
}