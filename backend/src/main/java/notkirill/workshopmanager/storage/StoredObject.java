package notkirill.workshopmanager.storage;

import java.io.InputStream;

public record StoredObject(InputStream data, String fileName, String contentType, long size) {
}
