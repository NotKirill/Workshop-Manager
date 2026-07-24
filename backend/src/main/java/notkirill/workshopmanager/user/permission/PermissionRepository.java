package notkirill.workshopmanager.user.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Short> {
    Optional<Permission> findByName(String name);

    @Query("SELECT p FROM Permission p WHERE p.id IN :permissionIds")
    List<Permission> findAllByIds(short[] permissionIds);
}