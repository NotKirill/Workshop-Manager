package notkirill.workshopmanager.media.web;

import notkirill.workshopmanager.media.MediaItemService;
import notkirill.workshopmanager.storage.StoredObject;
import notkirill.workshopmanager.storage.web.StoredObjectResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.Instant;

@RestController
@RequestMapping("/api/public/downloads/media")
public class MediaDownloadController {
    private final MediaItemService mediaItemService;
    private final Clock clock;

    @Autowired
    public MediaDownloadController(MediaItemService mediaItemService, Clock clock) {
        this.mediaItemService = mediaItemService;
        this.clock = clock;
    }

    @GetMapping("/{mediaItemId}")
    public ResponseEntity<InputStreamResource> download(@PathVariable long mediaItemId,
                                                        @RequestParam("exp") long expiresAt,
                                                        @RequestParam("sig") String signature) {
        StoredObject object = mediaItemService.openFile(mediaItemId, expiresAt, signature);
        return StoredObjectResponses.inline(object, Math.max(0, expiresAt - Instant.now(clock).getEpochSecond()));
    }
}
