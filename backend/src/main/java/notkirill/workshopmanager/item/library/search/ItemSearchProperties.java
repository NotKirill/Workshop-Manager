package notkirill.workshopmanager.item.library.search;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("application.search.item")
@Validated

public record ItemSearchProperties(
        @Name("max-query-length") @Positive Integer maxQueryLength,
        @Name("default-page-size") @Positive Integer defaultPageSize,
        @Name("max-page-size") @Positive Integer maxPageSize,
        @Name("max-depth") @Positive Integer maxDepth

) {
    public ItemSearchProperties {
        if (maxQueryLength == null) {
            maxQueryLength = 1000;
        }
        if (defaultPageSize == null) {
            defaultPageSize = 20;
        }
        if (maxPageSize == null) {
            maxPageSize = 50;
        }
        if (maxDepth == null) {
            maxDepth = 40;
        }
    }
}
