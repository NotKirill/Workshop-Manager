package notkirill.workshopmanager.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DirectoriesTest {
    @TempDir
    Path temp;

    @Test
    void deletesEverythingInsideButNotWhatLinksPointTo() throws IOException {
        Path folder = Files.createDirectories(temp.resolve("job/steamapps/content"));
        Files.writeString(folder.resolve("mod.lua"), "x");
        Path outside = Files.writeString(temp.resolve("keep.txt"), "server file");
        Files.createSymbolicLink(folder.resolve("link.txt"), outside);

        Directories.deleteRecursively(temp.resolve("job"));

        assertThat(temp.resolve("job")).doesNotExist();
        assertThat(outside).hasContent("server file");
    }
}
