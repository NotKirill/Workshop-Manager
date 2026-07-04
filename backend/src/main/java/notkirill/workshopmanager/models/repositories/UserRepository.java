package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}