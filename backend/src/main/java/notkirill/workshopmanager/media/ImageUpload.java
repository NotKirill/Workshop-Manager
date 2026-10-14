package notkirill.workshopmanager.media;

import org.springframework.core.io.InputStreamSource;

public record ImageUpload(String fileName, long size, InputStreamSource data) {
}
