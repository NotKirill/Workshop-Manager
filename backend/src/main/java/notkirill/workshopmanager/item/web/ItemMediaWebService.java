package notkirill.workshopmanager.item.web;

import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.media.ImageUpload;
import notkirill.workshopmanager.media.MediaLinkService;
import notkirill.workshopmanager.media.MediaService;
import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.media.web.MediaResponse;
import notkirill.workshopmanager.media.web.MediaResponseMapper;
import notkirill.workshopmanager.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ItemMediaWebService {
    private final PersonalItemService personalService;
    private final MediaService mediaService;
    private final MediaLinkService mediaLinkService;
    private final MediaResponseMapper mediaResponseMapper;

    @Autowired
    public ItemMediaWebService(PersonalItemService personalService, MediaService mediaService,
                               MediaLinkService mediaLinkService, MediaResponseMapper mediaResponseMapper) {
        this.personalService = personalService;
        this.mediaService = mediaService;
        this.mediaLinkService = mediaLinkService;
        this.mediaResponseMapper = mediaResponseMapper;
    }

    public List<MediaResponse> getMedia(long id, UserPrincipal principal) {
        return mediaService.list(principal.getUserId(), EntityType.ITEM, internalIdOf(id, principal)).stream()
                .map(mediaResponseMapper::toResponse)
                .toList();
    }

    public MediaResponse uploadMedia(long id, MultipartFile file, String purpose, UserPrincipal principal) {
        ImageUpload upload = new ImageUpload(file.getOriginalFilename(), file.getSize(), file);
        return mediaResponseMapper.toResponse(mediaService.uploadImage(
                principal.getUserId(), EntityType.ITEM,
                internalIdOf(id, principal), upload, purpose
        ));
    }

    public MediaResponse addEmbed(long id, String url, String purpose, UserPrincipal principal) {
        return mediaResponseMapper.toResponse(mediaService.addEmbed(
                principal.getUserId(), EntityType.ITEM,
                internalIdOf(id, principal), url, purpose
        ));
    }

    public MediaResponse addMediaLink(long id, long mediaItemId, String purpose, UserPrincipal principal) {
        return mediaResponseMapper.toResponse(mediaService.addLink(
                principal.getUserId(), EntityType.ITEM,
                internalIdOf(id, principal), mediaItemId, purpose
        ));
    }

    public void updateMedia(long id, long linkId, String purpose, Boolean hidden, UserPrincipal principal) {
        mediaLinkService.updateLink(
                principal.getUserId(), EntityType.ITEM, internalIdOf(id, principal), linkId,
                purpose, hidden
        );
    }

    public void deleteMedia(long id, long linkId, UserPrincipal principal) {
        mediaLinkService.deleteLink(principal.getUserId(), EntityType.ITEM, internalIdOf(id, principal), linkId);
    }

    public void reorderMedia(long id, List<Long> linkIds, UserPrincipal principal) {
        mediaLinkService.reorder(principal.getUserId(), EntityType.ITEM, internalIdOf(id, principal), linkIds);
    }

    private long internalIdOf(long personalId, UserPrincipal principal) {
        return personalService.requireOwned(principal.getUserId(), personalId).getInternalMetadataId();
    }
}
