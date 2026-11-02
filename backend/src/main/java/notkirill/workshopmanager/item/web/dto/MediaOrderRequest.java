package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MediaOrderRequest(
        @JsonProperty("ids") @NotNull List<@NotNull Long> ids
) {
}
