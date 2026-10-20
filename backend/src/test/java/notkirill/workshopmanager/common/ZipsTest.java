package notkirill.workshopmanager.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

class ZipsTest {
    @TempDir
    Path temp;

    @Test
    void keepsStructureAndSkipsSymbolicLinks() throws IOException {
        Path source = Files.createDirectories(temp.resolve("item"));
        Files.writeString(Files.createDirectories(source.resolve("lua")).resolve("init.lua"), "print(1)");
        Files.createDirectories(source.resolve("empty"));
        Path secret = Files.writeString(temp.resolve("secret.txt"), "server file");
        Files.createSymbolicLink(source.resolve("link.txt"), secret);
        Path zip = temp.resolve("item.zip");

        Zips.zipDirectory(source, zip);

        assertThat(entries(zip)).containsExactly("empty/", "lua/", "lua/init.lua");
    }

    private static List<String> entries(Path zip) throws IOException {
        List<String> names = new ArrayList<>();
        try (ZipInputStream in = new ZipInputStream(Files.newInputStream(zip))) {
            for (ZipEntry entry = in.getNextEntry(); entry != null; entry = in.getNextEntry()) {
                names.add(entry.getName());
            }
        }
        return names;
    }
}
