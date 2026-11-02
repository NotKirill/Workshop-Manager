package notkirill.workshopmanager.item.web;

import jakarta.validation.Valid;
import notkirill.workshopmanager.item.web.dto.MediaOrderRequest;
import notkirill.workshopmanager.media.web.MediaEmbedRequest;
import notkirill.workshopmanager.media.web.MediaLinkRequest;
import notkirill.workshopmanager.media.web.MediaLinkUpdateRequest;
import notkirill.workshopmanager.media.web.MediaResponse;
import notkirill.workshopmanager.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@PreAuthorize("hasAuthority('ITEMS_READ')")
public class ItemMediaController {
    private final ItemMediaWebService mediaWebService;

    @Autowired
    public ItemMediaController(ItemMediaWebService mediaWebService) {
        this.mediaWebService = mediaWebService;
    }

    @GetMapping("/{id}/media")
    public List<MediaResponse> getMedia(@PathVariable long id, @AuthenticationPrincipal UserPrincipal principal) {
        return mediaWebService.getMedia(id, principal);
    }

    @PostMapping(value = "/{id}/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('MEDIA_UPLOAD')")
    @ResponseStatus(HttpStatus.CREATED)
    public MediaResponse uploadMedia(@PathVariable long id, @RequestPart("file") MultipartFile file,
                                     @RequestParam("purpose") String purpose,
                                     @AuthenticationPrincipal UserPrincipal principal) {
        return mediaWebService.uploadMedia(id, file, purpose, principal);
    }

    @PostMapping("/{id}/media/embed")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.CREATED)
    public MediaResponse addEmbed(@PathVariable long id, @Valid @RequestBody MediaEmbedRequest request,
                                  @AuthenticationPrincipal UserPrincipal principal) {
        return mediaWebService.addEmbed(id, request.url(), request.purpose(), principal);
    }

    @PostMapping("/{id}/media/links")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.CREATED)
    public MediaResponse addMediaLink(@PathVariable long id, @Valid @RequestBody MediaLinkRequest request,
                                      @AuthenticationPrincipal UserPrincipal principal) {
        return mediaWebService.addMediaLink(id, request.mediaItemId(), request.purpose(), principal);
    }

    @PatchMapping("/{id}/media/{linkId}")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMedia(@PathVariable long id, @PathVariable long linkId,
                            @RequestBody MediaLinkUpdateRequest request,
                            @AuthenticationPrincipal UserPrincipal principal) {
        mediaWebService.updateMedia(id, linkId, request.purpose(), request.hidden(), principal);
    }

    @DeleteMapping("/{id}/media/{linkId}")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedia(@PathVariable long id, @PathVariable long linkId,
                            @AuthenticationPrincipal UserPrincipal principal) {
        mediaWebService.deleteMedia(id, linkId, principal);
    }

    @PutMapping("/{id}/media/order")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderMedia(@PathVariable long id, @Valid @RequestBody MediaOrderRequest request,
                             @AuthenticationPrincipal UserPrincipal principal) {
        mediaWebService.reorderMedia(id, request.ids(), principal);
    }
}
