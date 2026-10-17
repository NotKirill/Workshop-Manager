package notkirill.workshopmanager.content.web;

import notkirill.workshopmanager.content.ContentDownloadService;
import notkirill.workshopmanager.storage.StoredObject;
import notkirill.workshopmanager.storage.web.StoredObjectResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/downloads/content")
public class ContentDownloadController {
    private final ContentDownloadService contentDownloadService;

    @Autowired
    public ContentDownloadController(ContentDownloadService contentDownloadService) {
        this.contentDownloadService = contentDownloadService;
    }

    @GetMapping("/{contentId}/{revision}")
    public ResponseEntity<InputStreamResource> download(@PathVariable long contentId, @PathVariable long revision,
                                                        @RequestParam("exp") long expiresAt,
                                                        @RequestParam("sig") String signature) {
        StoredObject object = contentDownloadService.openRevision(contentId, revision, expiresAt, signature);
        return StoredObjectResponses.attachment(object);
    }
}
