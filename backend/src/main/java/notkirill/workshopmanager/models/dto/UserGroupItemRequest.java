package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record UserGroupItemRequest(
        @NotNull Long userGroupId,
        @NotNull Long itemPersonalMetadataId
) {
}
