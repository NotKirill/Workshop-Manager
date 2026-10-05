package notkirill.workshopmanager.game.web;

import jakarta.validation.Valid;
import notkirill.workshopmanager.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@PreAuthorize("hasAuthority('ITEMS_READ')")
public class GameController {
    private final GameWebService gameWebService;

    @Autowired
    public GameController(GameWebService gameWebService) {
        this.gameWebService = gameWebService;
    }

    @GetMapping
    public List<GameResponse> getAll(@AuthenticationPrincipal UserPrincipal principal) {
        return gameWebService.getAll(principal.getUserId());
    }

    @GetMapping("/{id}")
    public GameResponse getById(@PathVariable long id, @AuthenticationPrincipal UserPrincipal principal) {
        return gameWebService.getById(id, principal.getUserId());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse create(@Valid @RequestBody GameRequest request) {
        return gameWebService.create(request.name());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('GAMES_MANAGE')")
    public GameResponse rename(@PathVariable long id, @Valid @RequestBody GameRequest request) {
        return gameWebService.rename(id, request.name());
    }

    @PostMapping("/{id}/compatibility")
    @PreAuthorize("hasAuthority('GAMES_MANAGE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addCompatibility(@PathVariable long id, @Valid @RequestBody GameCompatibilityRequest request) {
        gameWebService.addCompatibility(id, request.targetGameId());
    }

    @DeleteMapping("/{id}/compatibility/{targetId}")
    @PreAuthorize("hasAuthority('GAMES_MANAGE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCompatibility(@PathVariable long id, @PathVariable long targetId) {
        gameWebService.removeCompatibility(id, targetId);
    }
}
