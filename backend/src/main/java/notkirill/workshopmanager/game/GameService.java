package notkirill.workshopmanager.game;

import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.exception.NotFoundException;
import notkirill.workshopmanager.game.models.GameCompatibility;
import notkirill.workshopmanager.game.models.GameMetadata;
import notkirill.workshopmanager.game.models.GameToModspaceId;
import notkirill.workshopmanager.game.repositories.GameCompatibilityRepository;
import notkirill.workshopmanager.game.repositories.GameMetadataRepository;
import notkirill.workshopmanager.game.repositories.GameToModspaceIdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final GameMetadataRepository gameRepository;
    private final GameToModspaceIdRepository mappingRepository;
    private final GameCompatibilityRepository compatibilityRepository;
    private final GameItemCountDao itemCountDao;

    @Autowired
    public GameService(GameMetadataRepository gameRepository, GameToModspaceIdRepository mappingRepository,
                       GameCompatibilityRepository compatibilityRepository, GameItemCountDao itemCountDao) {
        this.gameRepository = gameRepository;
        this.mappingRepository = mappingRepository;
        this.compatibilityRepository = compatibilityRepository;
        this.itemCountDao = itemCountDao;
    }

    @Transactional(readOnly = true)
    public List<GameMetadata> getAll() {
        return gameRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> itemCounts(long userId) {
        return itemCountDao.countByGame(userId);
    }

    @Transactional(readOnly = true)
    public long itemCount(long userId, long gameId) {
        return itemCountDao.count(userId, gameId);
    }

    @Transactional(readOnly = true)
    public Map<Long, List<GameToModspaceId>> modspaceIdsOf(Collection<Long> gameIds) {
        return mappingRepository.findByGameIdIn(gameIds).stream()
                .collect(Collectors.groupingBy(GameToModspaceId::getGameId));
    }

    @Transactional(readOnly = true)
    public Map<Long, List<Long>> compatibleGameIdsOf(Collection<Long> gameIds) {
        return compatibilityRepository.findBySourceGameIdIn(gameIds).stream()
                .collect(Collectors.groupingBy(
                        GameCompatibility::getSourceGameId,
                        Collectors.mapping(GameCompatibility::getTargetGameId, Collectors.toList())
                ));
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getNames(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return gameRepository.findAllById(new HashSet<>(ids)).stream()
                .collect(Collectors.toMap(GameMetadata::getId, GameMetadata::getName));
    }

    @Transactional
    public GameMetadata create(String name) {
        String normalized = name.strip();
        GameMetadata game = gameRepository.findFirstByNameIgnoreCase(normalized)
                .orElseGet(() -> gameRepository.save(new GameMetadata(normalized)));
        if (game.getId() == GameMetadata.UNKNOWN_ID) {
            throw new BadRequestException(
                    "GAME_NAME_RESERVED",
                    "\"" + game.getName() + "\" is reserved, choose another name"
            );
        }
        return game;
    }

    @Transactional
    public GameMetadata rename(long id, String name) {
        GameMetadata game = requireKnownGame(id);
        game.setNameRequestedAt(null);
        game.setName(name.strip());
        return gameRepository.save(game);
    }

    @Transactional
    public void addCompatibility(long sourceGameId, long targetGameId) {
        if (sourceGameId == targetGameId) {
            throw new BadRequestException("INVALID_COMPATIBILITY", "A game is always compatible with itself");
        }
        requireKnownGame(sourceGameId);
        requireKnownGame(targetGameId);
        compatibilityRepository.save(new GameCompatibility(sourceGameId, targetGameId));
    }

    @Transactional
    public void removeCompatibility(long sourceGameId, long targetGameId) {
        compatibilityRepository.deleteById(new GameCompatibility.CompositeId(sourceGameId, targetGameId));
    }

    public GameMetadata requireGame(long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("GAME_NOT_FOUND", "Game not found: " + id));
    }

    public GameMetadata requireKnownGame(long id) {
        if (id == GameMetadata.UNKNOWN_ID) {
            throw new BadRequestException("UNKNOWN_GAME", "Not available for items whose game is not known yet");
        }
        return requireGame(id);
    }

    public boolean exists(long id) {
        return gameRepository.existsById(id);
    }
}
