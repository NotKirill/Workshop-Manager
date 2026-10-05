package notkirill.workshopmanager.game.web;

import notkirill.workshopmanager.game.GameService;
import notkirill.workshopmanager.game.models.GameMetadata;
import notkirill.workshopmanager.game.models.GameToModspaceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GameWebService {
    private final GameService gameService;

    @Autowired
    public GameWebService(GameService gameService) {
        this.gameService = gameService;
    }

    public List<GameResponse> getAll(long userId) {
        return toResponses(gameService.getAll(), gameService.itemCounts(userId));
    }

    public GameResponse getById(long id, long userId) {
        GameMetadata game = gameService.requireGame(id);
        return toResponses(List.of(game), Map.of(id, gameService.itemCount(userId, id))).getFirst();
    }

    public GameResponse create(String name) {
        return toResponses(List.of(gameService.create(name)), Map.of()).getFirst();
    }

    public GameResponse rename(long id, String name) {
        return toResponses(List.of(gameService.rename(id, name)), Map.of()).getFirst();
    }

    public void addCompatibility(long sourceGameId, long targetGameId) {
        gameService.addCompatibility(sourceGameId, targetGameId);
    }

    public void removeCompatibility(long sourceGameId, long targetGameId) {
        gameService.removeCompatibility(sourceGameId, targetGameId);
    }

    private List<GameResponse> toResponses(List<GameMetadata> games, Map<Long, Long> counts) {
        List<Long> ids = games.stream().map(GameMetadata::getId).toList();
        Map<Long, List<GameToModspaceId>> mappings = gameService.modspaceIdsOf(ids);
        Map<Long, List<Long>> compatibility = gameService.compatibleGameIdsOf(ids);
        List<GameResponse> result = new ArrayList<>();
        for (GameMetadata game : games) {
            List<GameResponse.ModspaceId> modspaceIds = mappings.getOrDefault(game.getId(), List.of()).stream()
                    .map(m -> new GameResponse.ModspaceId(m.getSourceModspace(), m.getExternalId()))
                    .toList();
            boolean namePending = game.getNameRequestedAt() != null;
            result.add(new GameResponse(
                    game.getId(), game.getName(), game.getSlug(), namePending, null,
                    modspaceIds, compatibility.getOrDefault(game.getId(), List.of()),
                    counts.getOrDefault(game.getId(), 0L)
            ));
        }
        return result;
    }
}
