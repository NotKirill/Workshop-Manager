package notkirill.workshopmanager.content.fetch;

import java.nio.file.Path;

public record FetchedContent(
        Path file,
        String fileName
) {
}
