package notkirill.workshopmanager.game.repositories;

import notkirill.workshopmanager.game.models.GameToModspaceId;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GameToModspaceIdRepository extends JpaRepository<GameToModspaceId, GameToModspaceId.CompositeId> {
    List<GameToModspaceId> findByGameId(long gameId);

    List<GameToModspaceId> findByGameIdIn(Collection<Long> gameIds);

    Optional<GameToModspaceId> findBySourceModspaceAndExternalId(ModspaceType sourceModspace, String externalId);
}
