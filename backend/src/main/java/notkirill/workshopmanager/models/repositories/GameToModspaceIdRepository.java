package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.GameToModspaceId;
import notkirill.workshopmanager.models.enums.ModspaceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameToModspaceIdRepository extends JpaRepository<GameToModspaceId, Long> {
    List<GameToModspaceId> findByGameId(long gameId);

    Optional<GameToModspaceId> findBySourceModspaceAndExternalId(ModspaceType sourceModspace, String externalId);
}