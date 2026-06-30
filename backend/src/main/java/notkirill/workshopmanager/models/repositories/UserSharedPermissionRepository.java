package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserSharedPermission;
import notkirill.workshopmanager.models.idClasses.UserSharedPermissionComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSharedPermissionRepository extends JpaRepository<UserSharedPermission, UserSharedPermissionComplexKey> {
    Optional<UserSharedPermission> findByUserProviderId(long userProviderId);

    Optional<UserSharedPermission> findByUserConsumerId(long userConsumerId);
}