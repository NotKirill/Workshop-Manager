package notkirill.workshopmanager.item.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import notkirill.workshopmanager.content.web.ContentSummaryResponse;
import notkirill.workshopmanager.content.web.ContentUploadRequest;
import notkirill.workshopmanager.content.web.ContentUploadResponse;
import notkirill.workshopmanager.content.web.DownloadLinkResponse;
import notkirill.workshopmanager.item.web.dto.BulkResultResponse;
import notkirill.workshopmanager.item.web.dto.ContentSettingsRequest;
import notkirill.workshopmanager.item.web.dto.ItemIdsRequest;
import notkirill.workshopmanager.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@PreAuthorize("hasAuthority('ITEMS_READ')")
public class ItemContentController {
    private final ItemContentWebService contentWebService;

    @Autowired
    public ItemContentController(ItemContentWebService contentWebService) {
        this.contentWebService = contentWebService;
    }

    @PostMapping("/bulk/backup")
    @PreAuthorize("hasAuthority('CONTENT_IMPORT')")
    public BulkResultResponse bulkBackup(@Valid @RequestBody ItemIdsRequest request,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        return contentWebService.requestBackup(request.itemIds(), principal);
    }

    @GetMapping("/{id}/content")
    public ContentSummaryResponse getContent(@PathVariable long id,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        return contentWebService.getContent(id, principal);
    }

    @PostMapping("/{id}/content/backup")
    @PreAuthorize("hasAuthority('CONTENT_IMPORT')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void requestBackup(@PathVariable long id, @AuthenticationPrincipal UserPrincipal principal) {
        contentWebService.requestBackup(List.of(id), principal);
    }

    @PostMapping("/{id}/content/uploads")
    @PreAuthorize("hasAuthority('CONTENT_IMPORT')")
    @ResponseStatus(HttpStatus.CREATED)
    public ContentUploadResponse startContentUpload(@PathVariable long id,
                                                    @Valid @RequestBody ContentUploadRequest request,
                                                    @AuthenticationPrincipal UserPrincipal principal) {
        return contentWebService.startUpload(id, request, principal);
    }

    @GetMapping("/{id}/content/uploads/{uploadId}")
    @PreAuthorize("hasAuthority('CONTENT_IMPORT')")
    public ContentUploadResponse getContentUpload(@PathVariable long id, @PathVariable long uploadId,
                                                  @AuthenticationPrincipal UserPrincipal principal) {
        return contentWebService.getUpload(id, uploadId, principal);
    }

    @PatchMapping(value = "/{id}/content/uploads/{uploadId}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasAuthority('CONTENT_IMPORT')")
    public ContentUploadResponse appendContentUpload(@PathVariable long id, @PathVariable long uploadId,
                                                     @RequestHeader("Upload-Offset") long offset,
                                                     HttpServletRequest request,
                                                     @AuthenticationPrincipal UserPrincipal principal)
            throws IOException {
        return contentWebService.appendUpload(
                id, uploadId, offset, request.getContentLengthLong(),
                request.getInputStream(), principal
        );
    }

    @DeleteMapping("/{id}/content/uploads/{uploadId}")
    @PreAuthorize("hasAuthority('CONTENT_IMPORT')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelContentUpload(@PathVariable long id, @PathVariable long uploadId,
                                    @AuthenticationPrincipal UserPrincipal principal) {
        contentWebService.cancelUpload(id, uploadId, principal);
    }

    @PostMapping("/{id}/content/revisions/{revision}/link")
    public DownloadLinkResponse createDownloadLink(@PathVariable long id, @PathVariable long revision,
                                                   @AuthenticationPrincipal UserPrincipal principal) {
        return contentWebService.createDownloadLink(id, revision, principal);
    }

    @DeleteMapping("/{id}/content/revisions/{revision}")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteRevision(@PathVariable long id, @PathVariable long revision,
                               @AuthenticationPrincipal UserPrincipal principal) {
        contentWebService.deleteRevision(id, revision, principal);
    }

    @DeleteMapping("/{id}/content")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteAllContent(@PathVariable long id, @AuthenticationPrincipal UserPrincipal principal) {
        contentWebService.deleteAll(id, principal);
    }

    @PutMapping("/{id}/content/settings")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateContentSettings(@PathVariable long id, @Valid @RequestBody ContentSettingsRequest request,
                                      @AuthenticationPrincipal UserPrincipal principal) {
        contentWebService.setMaxRevisions(id, request.maxRevisions(), principal);
    }
}
