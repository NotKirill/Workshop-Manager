package notkirill.workshopmanager.user.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RenameProfileRequest(
        @JsonProperty("name") @NotBlank @Size(max = 100) String name
) {
}
