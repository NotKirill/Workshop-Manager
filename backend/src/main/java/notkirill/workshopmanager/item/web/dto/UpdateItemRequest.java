package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import notkirill.workshopmanager.common.Texts;

public record UpdateItemRequest(
        @JsonProperty("title") @Size(max = 500) String title,
        @JsonProperty("description") @Size(max = 100_000) String description,
        @JsonProperty("user_commentary") @Size(max = 100_000) String userCommentary,
        @JsonProperty("auto_backup") @NotNull Boolean autoBackup
) {
    @Override
    public String title() {
        return Texts.blankToNull(title);
    }

    @Override
    public String description() {
        return Texts.blankToNull(description);
    }

    @Override
    public String userCommentary() {
        return Texts.blankToNull(userCommentary);
    }
}
