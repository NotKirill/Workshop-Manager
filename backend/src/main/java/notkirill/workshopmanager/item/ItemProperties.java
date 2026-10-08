package notkirill.workshopmanager.item;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;


@ConfigurationProperties("application.items")
@Validated
public record ItemProperties(
        @Name("max-dependency-depth") Integer maxDependencyDepth,
        @Name("max-dependency-items") Integer maxDependencyItems
) {
    public ItemProperties {
        if (maxDependencyDepth == null || maxDependencyDepth < 1) {
            maxDependencyDepth = 3;
        }
        if (maxDependencyItems == null || maxDependencyItems < 1) {
            maxDependencyItems = 200;
        }
    }
}
