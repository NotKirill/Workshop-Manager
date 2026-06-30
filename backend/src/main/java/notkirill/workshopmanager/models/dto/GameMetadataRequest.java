package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

public record GameMetadataRequest(
        @NotNull String name
) {
}