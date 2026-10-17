package notkirill.workshopmanager.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class Directories {
    private static final Logger log = LoggerFactory.getLogger(Directories.class);

    private Directories() {
    }

    public static void deleteRecursively(Path directory) {
        if (!Files.exists(directory)) {
            return;
        }
        List<Path> paths;
        try (Stream<Path> walk = Files.walk(directory)) {
            // Deepest paths first
            paths = walk.sorted(Comparator.reverseOrder()).toList();
        } catch (IOException e) {
            log.warn("Could not list temporary folder {}: {}", directory, e.getMessage());
            return;
        }
        for (Path path : paths) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.warn("Could not delete temporary file {}: {}", path, e.getMessage());
            }
        }
    }
}
