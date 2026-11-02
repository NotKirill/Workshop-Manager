package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterDependenciesRequest(
        @JsonProperty("auto_backup") Boolean autoBackup
) {
}
