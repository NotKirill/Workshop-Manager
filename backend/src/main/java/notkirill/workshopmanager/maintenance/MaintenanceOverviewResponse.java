package notkirill.workshopmanager.maintenance;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MaintenanceOverviewResponse(
        @JsonProperty("steam_api_key_configured") boolean steamApiKeyConfigured,
        @JsonProperty("steamcmd_configured") boolean steamcmdConfigured,
        @JsonProperty("storage_type") String storageType
) {
}
