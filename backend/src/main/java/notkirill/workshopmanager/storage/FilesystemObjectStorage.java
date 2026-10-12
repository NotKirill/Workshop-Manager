package notkirill.workshopmanager.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FilesystemObjectStorage implements ObjectStorage {
    private final Path root;

    public FilesystemObjectStorage(Path root) {
        this.root = root.toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.root);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create storage root " + this.root, e);
        }
    }

    @Override
    public void put(String key, Path file, String contentType) {
        Path target = resolve(key);
        try {
            Files.createDirectories(target.getParent());
            Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot store object " + key, e);
        }
    }

    @Override
    public void put(String key, InputStream data, long size, String contentType) {
        Path target = resolve(key);
        try {
            Files.createDirectories(target.getParent());
            Files.copy(data, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot store object " + key, e);
        }
    }

    @Override
    public InputStream get(String key) {
        try {
            return Files.newInputStream(resolve(key));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read object " + key, e);
        }
    }

    @Override
    public boolean exists(String key) {
        return Files.exists(resolve(key));
    }

    @Override
    public void delete(String key) {
        Path target = resolve(key);
        try {
            Files.deleteIfExists(target);
            deleteFolderIfEmpty(target.getParent());
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot delete object " + key, e);
        }
    }

    private void deleteFolderIfEmpty(Path folder) throws IOException {
        if (folder.equals(root)) {
            return;
        }
        try {
            Files.deleteIfExists(folder);
        } catch (DirectoryNotEmptyException e) {
            // Other objects still live in this folder
        }
    }

    private Path resolve(String key) {
        Path path = root.resolve(key).normalize();
        if (!path.startsWith(root)) {
            throw new IllegalArgumentException("Invalid storage key");
        }
        return path;
    }
}
