package notkirill.workshopmanager.item.organization.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CollectionRequest(
        @JsonProperty("name") @NotBlank @Size(max = 100) String name,
        @JsonProperty("game_id") @NotNull Long gameId
) {
}
