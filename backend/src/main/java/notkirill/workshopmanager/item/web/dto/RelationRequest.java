package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.item.enums.RelationType;

public record RelationRequest(
        @JsonProperty("target_id") @NotNull Long targetId,
        @JsonProperty("relation") @NotNull RelationType relation
) {
}
