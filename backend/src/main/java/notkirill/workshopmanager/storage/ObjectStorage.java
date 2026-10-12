package notkirill.workshopmanager.storage;

import java.io.InputStream;
import java.nio.file.Path;

public interface ObjectStorage {
    void put(String key, Path file, String contentType);

    void put(String key, InputStream data, long size, String contentType);

    InputStream get(String key);

    boolean exists(String key);

    void delete(String key);
}
