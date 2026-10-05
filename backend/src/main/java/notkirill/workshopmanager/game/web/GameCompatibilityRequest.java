package notkirill.workshopmanager.game.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record GameCompatibilityRequest(
        @JsonProperty("target_game_id") @NotNull Long targetGameId
) {
}
