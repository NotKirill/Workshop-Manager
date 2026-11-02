package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ContentSettingsRequest(
        @JsonProperty("max_revisions") @NotNull @Min(1) @Max(50) Short maxRevisions
) {
}
