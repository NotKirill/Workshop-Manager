package notkirill.workshopmanager.game.lookup;

import notkirill.workshopmanager.game.models.GameMetadata;
import notkirill.workshopmanager.game.models.GameToModspaceId;
import notkirill.workshopmanager.game.repositories.GameMetadataRepository;
import notkirill.workshopmanager.game.repositories.GameToModspaceIdRepository;
import notkirill.workshopmanager.modspace.ModspaceAdapter;
import notkirill.workshopmanager.modspace.ModspaceRegistry;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GameLookupService {
    private static final Logger log = LoggerFactory.getLogger(GameLookupService.class);

    private final GameToModspaceIdRepository mappingRepository;
    private final GameMetadataRepository gameRepository;
    private final ModspaceRegistry modspaceRegistry;
    private final GameLookupProperties properties;
    private final TransactionTemplate transactionTemplate;
    private final Clock clock;

    @Autowired
    public GameLookupService(GameToModspaceIdRepository mappingRepository, GameMetadataRepository gameRepository,
                             ModspaceRegistry modspaceRegistry, GameLookupProperties properties,
                             PlatformTransactionManager transactionManager, Clock clock) {
        this.mappingRepository = mappingRepository;
        this.gameRepository = gameRepository;
        this.modspaceRegistry = modspaceRegistry;
        this.properties = properties;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.clock = clock;
    }

    public LookupReport lookUpQueued() {
        List<GameMetadata> queued = gameRepository.findByNameRequestedAtIsNotNullOrderByNameRequestedAtAsc(
                PageRequest.of(0, properties.maxPerRun()));

        int named = 0;
        int unknown = 0;
        int failed = 0;
        Set<ModspaceType> skipped = EnumSet.noneOf(ModspaceType.class);
        for (GameMetadata game : queued) {
            switch (lookUp(game, skipped)) {
                case NAMED -> named++;
                case UNKNOWN -> unknown++;
                case FAILED -> failed++;
                case WAITING -> {
                    // The mark stays for a later run
                }
            }
        }
        return new LookupReport(named, unknown, failed);
    }

    public long countQueued() {
        return gameRepository.countByNameRequestedAtIsNotNull();
    }

    private Outcome lookUp(GameMetadata game, Set<ModspaceType> skipped) {
        boolean asked = false;
        for (GameToModspaceId mapping : mappingRepository.findByGameId(game.getId())) {
            ModspaceType modspace = mapping.getSourceModspace();
            if (skipped.contains(modspace)) {
                continue;
            }
            Optional<ModspaceAdapter> adapter = modspaceRegistry.find(modspace)
                    .filter(ModspaceAdapter::supportsGameNames);
            if (adapter.isEmpty()) {
                skipped.add(modspace);
                continue;
            }
            asked = true;
            Optional<String> name;
            try {
                name = adapter.get().fetchGameName(mapping.getExternalId())
                        .map(String::strip)
                        .filter(n -> !n.isEmpty());
            } catch (RuntimeException e) {
                // Quota used up or modspace down
                log.warn("Name of game {} could not be looked up in {}: {}", game.getId(), modspace, e.getMessage());
                transactionTemplate.executeWithoutResult(status ->
                                                                 gameRepository.requeueNameRequest(
                                                                         game.getId(),
                                                                         OffsetDateTime.now(
                                                                                 clock)
                                                                 ));
                skipped.add(modspace);
                return Outcome.FAILED;
            }
            if (name.isPresent()) {
                return apply(game.getId(), name.get()) ? Outcome.NAMED : Outcome.WAITING;
            }
        }
        if (!asked) {
            return Outcome.WAITING;
        }
        return apply(game.getId(), null) ? Outcome.UNKNOWN : Outcome.WAITING;
    }

    private boolean apply(long gameId, String name) {
        return Boolean.TRUE.equals(transactionTemplate.execute(status -> {
            if (gameRepository.clearNameRequest(gameId) == 0) {
                return false;
            }
            if (name != null) {
                gameRepository.findById(gameId).ifPresent(game -> {
                    game.setNameRequestedAt(null);
                    game.setName(name);
                    gameRepository.save(game);
                });
            }
            return true;
        }));
    }

    private enum Outcome {
        NAMED, UNKNOWN, FAILED, WAITING
    }

    public record LookupReport(int named, int unknown, int failed) {
    }
}
