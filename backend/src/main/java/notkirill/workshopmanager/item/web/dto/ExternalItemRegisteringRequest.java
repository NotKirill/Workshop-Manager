package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import notkirill.workshopmanager.item.registration.ItemRegistrationService;
import notkirill.workshopmanager.modspace.ModspaceType;

import java.util.List;

public record ExternalItemRegisteringRequest(
        @JsonProperty("items") @NotEmpty @Size(max = 10) List<@NotNull @Valid ModspaceItems> items,
        @JsonProperty("auto_backup") Boolean autoBackup,
        @JsonProperty("include_dependencies") Boolean includeDependencies
) {
    public ExternalItemRegisteringRequest {
        if (autoBackup == null) {
            autoBackup = false;
        }
        if (includeDependencies == null) {
            includeDependencies = false;
        }
    }

    @JsonIgnore
    @AssertTrue
    public boolean isItemCountWithinLimit() {
        return items == null || items.stream()
                .mapToInt(group -> group == null || group.originalIds() == null ? 0 : group.originalIds().size())
                .sum() <= ItemRegistrationService.MAX_ITEMS_PER_REQUEST;
    }

    public record ModspaceItems(
            @JsonProperty("modspace") @NotNull ModspaceType modspace,
            @JsonProperty("original_ids") @NotEmpty @Size(max = ItemRegistrationService.MAX_ITEMS_PER_REQUEST)
            List<@NotBlank @Size(max = 100) String> originalIds
    ) {
    }
}
