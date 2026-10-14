package notkirill.workshopmanager.storage.web;

import notkirill.workshopmanager.storage.StoredObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;

public final class StoredObjectResponses {
    private StoredObjectResponses() {
    }

    public static ResponseEntity<InputStreamResource> attachment(StoredObject object) {
        return build(object, ContentDisposition.attachment(), new HttpHeaders());
    }

    public static ResponseEntity<InputStreamResource> inline(StoredObject object, long maxAgeSeconds) {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("private, max-age=" + maxAgeSeconds);
        return build(object, ContentDisposition.inline(), headers);
    }

    private static ResponseEntity<InputStreamResource> build(StoredObject object,
                                                             ContentDisposition.Builder disposition,
                                                             HttpHeaders headers) {
        headers.setContentDisposition(disposition.filename(object.fileName(), StandardCharsets.UTF_8).build());
        headers.set("X-Content-Type-Options", "nosniff");
        headers.set(
                "Content-Security-Policy",
                "default-src 'none'; img-src 'self'; style-src 'unsafe-inline'; sandbox"
        );
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(object.contentType()))
                .contentLength(object.size())
                .body(new InputStreamResource(object.data()));
    }
}
