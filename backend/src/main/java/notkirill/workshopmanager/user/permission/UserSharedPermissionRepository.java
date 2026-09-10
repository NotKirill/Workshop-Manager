package notkirill.workshopmanager.user.permission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSharedPermissionRepository extends JpaRepository<UserSharedPermission, UserSharedPermission.CompositeId> {
    List<UserSharedPermission> findByUserProviderId(long userProviderId);

    List<UserSharedPermission> findByUserConsumerId(long userConsumerId);
}