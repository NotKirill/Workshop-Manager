package notkirill.workshopmanager.game;

import notkirill.workshopmanager.common.DatabaseLocks;
import notkirill.workshopmanager.game.models.GameMetadata;
import notkirill.workshopmanager.game.models.GameToModspaceId;
import notkirill.workshopmanager.game.repositories.GameMetadataRepository;
import notkirill.workshopmanager.game.repositories.GameToModspaceIdRepository;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class GameResolutionService {
    private final GameMetadataRepository gameRepository;
    private final GameToModspaceIdRepository mappingRepository;
    private final DatabaseLocks databaseLocks;
    private final Clock clock;

    @Autowired
    public GameResolutionService(GameMetadataRepository gameRepository,
                                 GameToModspaceIdRepository mappingRepository, DatabaseLocks databaseLocks,
                                 Clock clock) {
        this.gameRepository = gameRepository;
        this.mappingRepository = mappingRepository;
        this.databaseLocks = databaseLocks;
        this.clock = clock;
    }

    public Optional<Long> find(ModspaceType modspace, String externalId) {
        return mappingRepository.findBySourceModspaceAndExternalId(modspace, externalId)
                .map(GameToModspaceId::getGameId);
    }

    @Transactional
    public long resolve(ModspaceType modspace, String externalId, String name, String placeholderName) {
        Optional<Long> existing = find(modspace, externalId);
        if (existing.isPresent()) {
            return existing.get();
        }
        databaseLocks.lock("game:" + modspace + ":" + externalId);
        existing = find(modspace, externalId);
        if (existing.isPresent()) {
            return existing.get();
        }
        boolean named = name != null && !name.isBlank();
        GameMetadata newGame = new GameMetadata(named ? name.strip() : placeholderName);
        if (!named) {
            newGame.setNameRequestedAt(OffsetDateTime.now(clock));
        }
        GameMetadata game = gameRepository.save(newGame);
        mappingRepository.save(new GameToModspaceId(game.getId(), modspace, externalId));
        return game.getId();
    }
}
