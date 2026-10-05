package notkirill.workshopmanager.game.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GameRequest(
        @JsonProperty("name") @NotBlank @Size(max = 200) String name
) {
}
