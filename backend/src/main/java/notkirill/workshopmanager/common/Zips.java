package notkirill.workshopmanager.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class Zips {
    private static final Logger log = LoggerFactory.getLogger(Zips.class);

    private Zips() {
    }

    public static void zipDirectory(Path sourceDir, Path zipFile) throws IOException {
        List<Path> paths;
        try (Stream<Path> walk = Files.walk(sourceDir)) {
            paths = walk.filter(path -> !path.equals(sourceDir)).sorted().toList();
        }
        int skippedLinks = 0;
        try (OutputStream out = Files.newOutputStream(zipFile); ZipOutputStream zip = new ZipOutputStream(out)) {
            for (Path path : paths) {
                String entryName = sourceDir.relativize(path).toString().replace('\\', '/');
                if (Files.isSymbolicLink(path)) {
                    skippedLinks++;
                } else if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                    zip.putNextEntry(new ZipEntry(entryName + "/"));
                    zip.closeEntry();
                } else if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
                    zip.putNextEntry(new ZipEntry(entryName));
                    Files.copy(path, zip);
                    zip.closeEntry();
                }
            }
        }
        if (skippedLinks > 0) {
            log.warn("Skipped {} symbolic link(s) while zipping {}", skippedLinks, sourceDir);
        }
    }
}
