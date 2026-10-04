package notkirill.workshopmanager.modspace.steam;

import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopExtended;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopItem;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopVoteData;
import notkirill.workshopmanager.modspace.steam.repositories.SteamWorkshopExtendedRepository;
import notkirill.workshopmanager.modspace.steam.repositories.SteamWorkshopItemRepository;
import notkirill.workshopmanager.modspace.steam.repositories.SteamWorkshopVoteDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

@Service
public class SteamItemWriterService {
    private final SteamWorkshopItemRepository itemRepository;
    private final SteamWorkshopExtendedRepository extendedRepository;
    private final SteamWorkshopVoteDataRepository voteRepository;
    private final SteamTagService tagService;

    @Autowired
    public SteamItemWriterService(SteamWorkshopItemRepository itemRepository,
                                  SteamWorkshopExtendedRepository extendedRepository,
                                  SteamWorkshopVoteDataRepository voteRepository,
                                  SteamTagService tagService) {
        this.itemRepository = itemRepository;
        this.extendedRepository = extendedRepository;
        this.voteRepository = voteRepository;
        this.tagService = tagService;
    }

    @Transactional
    public SaveResult save(SteamPublishedFile file) {
        SteamWorkshopItem item = itemRepository.findByPublishedFileId(file.publishedFileId())
                .orElseGet(() -> new SteamWorkshopItem(file.publishedFileId()));
        boolean mediaChanged = !Objects.equals(item.getPreviewUrl(), file.previewUrl());
        item.setCreator(file.creator());
        item.setCreatorAppId(file.creatorAppId());
        item.setConsumerAppId(file.consumerAppId());
        item.setFileSize(file.fileSize() != null ? String.valueOf(file.fileSize()) : null);
        item.setPreviewUrl(file.previewUrl());
        item.setTitle(file.title());
        item.setDescription(file.description());
        item.setTimeCreated(file.timeCreated());
        item.setTimeUpdated(file.timeUpdated());
        item.setVisibility(file.visibility());
        item.setSubscriptions(file.subscriptions());
        item.setFavorited(file.favorited());
        item.setLifetimeSubscriptions(file.lifetimeSubscriptions());
        item.setLifetimeFavorited(file.lifetimeFavorited());
        long localId = itemRepository.save(item).getLocalId();
        if (file.details() != null) {
            mediaChanged |= !Objects.equals(
                    extendedRepository.findById(localId)
                            .map(SteamWorkshopExtended::getPreviews).orElse(null), file.details().previews()
            );
        }

        // Only the keyed endpoint sends these; without it, what an earlier keyed sync stored stays as it is
        boolean dependenciesChanged = file.details() != null
                && saveDetails(localId, file.timeUpdated(), file.details());
        tagService.replaceTags(localId, file.consumerAppId(), file.tags());
        return new SaveResult(dependenciesChanged, mediaChanged);
    }

    @Transactional
    public void delete(String publishedFileId) {
        itemRepository.findByPublishedFileId(publishedFileId).ifPresent(item -> {
            long localId = item.getLocalId();
            tagService.deleteLinks(localId);
            voteRepository.findById(localId).ifPresent(voteRepository::delete);
            extendedRepository.findById(localId).ifPresent(extendedRepository::delete);
            itemRepository.delete(item);
        });
    }

    private boolean saveDetails(long localId, OffsetDateTime timeUpdated, SteamPublishedFile.Details details) {
        Optional<SteamWorkshopExtended> stored = extendedRepository.findById(localId);
        boolean dependenciesChanged = stored.isPresent()
                && !new HashSet<>(SteamJsonUtil.childIds(stored.get().getChildren()))
                .equals(new HashSet<>(SteamJsonUtil.childIds(details.children())));
        SteamWorkshopExtended extended = stored.orElseGet(() -> new SteamWorkshopExtended(
                localId, null, null, (short) 0, null, null, null, false, false, new short[0], null
        ));
        extended.setPreviewFileSize(details.previewFileSize());
        extended.setShortDescription(details.shortDescription());
        extended.setNumChildren(details.numChildren());
        extended.setPreviews(details.previews());
        extended.setChildren(details.children());
        extended.setKvtags(details.kvtags());
        extended.setMaybeInappropriateSex(details.maybeInappropriateSex());
        extended.setMaybeInappropriateViolence(details.maybeInappropriateViolence());
        extended.setContentDescriptorIds(details.contentDescriptorIds());
        extended.setTimeUpdated(timeUpdated);
        extendedRepository.save(extended);

        if (details.votes() != null) {
            SteamWorkshopVoteData votes = voteRepository.findById(localId)
                    .orElseGet(() -> new SteamWorkshopVoteData(localId, 0, 0, 0));
            votes.setScore(details.votes().score());
            votes.setVotesUp(details.votes().votesUp());
            votes.setVotesDown(details.votes().votesDown());
            voteRepository.save(votes);
        }
        return dependenciesChanged;
    }

    public record SaveResult(boolean dependenciesChanged, boolean mediaChanged) {
    }
}
