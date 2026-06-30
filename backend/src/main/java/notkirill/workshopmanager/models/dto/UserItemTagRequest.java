package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record UserItemTagRequest(
        @NotNull Long tagId,
        @NotNull Long itemPersonalMetadataId
) {
}
