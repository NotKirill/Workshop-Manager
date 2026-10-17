package notkirill.workshopmanager.content.fetch;

import notkirill.workshopmanager.modspace.ModspaceType;

import java.nio.file.Path;

public interface ContentFetcher {
    ModspaceType type();

    boolean isAvailable();

    FetchedContent fetch(String originalId, Path workDir);
}
