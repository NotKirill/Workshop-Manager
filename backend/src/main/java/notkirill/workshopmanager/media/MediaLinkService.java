package notkirill.workshopmanager.media;

import jakarta.annotation.Nullable;
import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.exception.NotFoundException;
import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.media.models.MediaItem;
import notkirill.workshopmanager.media.models.MediaLink;
import notkirill.workshopmanager.media.models.MediaLinkOverride;
import notkirill.workshopmanager.media.repositories.MediaLinkOverrideRepository;
import notkirill.workshopmanager.media.repositories.MediaLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MediaLinkService {
    private final MediaLinkRepository linkRepository;
    private final MediaLinkOverrideRepository overrideRepository;
    private final MediaItemService itemService;

    @Autowired
    public MediaLinkService(MediaLinkRepository linkRepository, MediaLinkOverrideRepository overrideRepository,
                            MediaItemService itemService) {
        this.linkRepository = linkRepository;
        this.overrideRepository = overrideRepository;
        this.itemService = itemService;
    }

    @Transactional(readOnly = true)
    public List<VisibleLink> view(long userId, EntityType entity, long entityId) {
        List<MediaLink> links = linkRepository.findVisible(entity, entityId, userId);
        Set<String> ownSinglePurposes = links.stream()
                .filter(link -> link.getOwnerUserId() != null && MediaPurposes.isSingle(link.getPurpose()))
                .map(MediaLink::getPurpose)
                .collect(Collectors.toSet());
        List<Long> sourceLinkIds = links.stream().filter(link -> link.getOwnerUserId() == null)
                .map(MediaLink::getId).toList();
        Map<Long, MediaLinkOverride> overrides = overrideRepository.findByUserIdAndLinkIdIn(userId, sourceLinkIds)
                .stream().collect(Collectors.toMap(MediaLinkOverride::getLinkId, Function.identity()));

        List<VisibleLink> entries = new ArrayList<>();
        for (MediaLink link : links) {
            if (link.getOwnerUserId() != null) {
                entries.add(new VisibleLink(link, link.getSortOrder(), false));
            } else if (!ownSinglePurposes.contains(link.getPurpose())) {
                MediaLinkOverride override = overrides.get(link.getId());
                boolean hidden = override != null && override.isHidden();
                int sortOrder = override != null && override.getSortOrder() != null
                        ? override.getSortOrder()
                        : link.getSortOrder();
                entries.add(new VisibleLink(link, sortOrder, hidden));
            }
        }
        entries.sort(Comparator.comparingInt(VisibleLink::sortOrder).thenComparingLong(entry -> entry.link().getId()));
        return entries;
    }


    @Transactional
    public VisibleLink createOwnLink(long userId, EntityType entity, long entityId, long mediaItemId, String purpose) {
        MediaPurposes.requireValid(purpose);
        int sortOrder = view(userId, entity, entityId).stream().mapToInt(VisibleLink::sortOrder).max().orElse(0) + 1;
        MediaLink link = linkRepository.save(new MediaLink(
                mediaItemId, entity, entityId, userId, null, purpose,
                sortOrder
        ));
        if (MediaPurposes.isSingle(purpose)) {
            removeOtherOwnLinks(userId, entity, entityId, purpose, link.getId());
        }
        return new VisibleLink(link, sortOrder, false);
    }

    @Transactional
    public VisibleLink addLink(long userId, EntityType entity, long entityId, long mediaItemId, String purpose) {
        MediaItem item = itemService.find(mediaItemId)
                .filter(found -> found.getOwnerUserId() == null
                        ? linkRepository.existsByMediaItemIdAndEntityAndEntityIdAndOwnerUserIdIsNull(
                        mediaItemId,
                        entity, entityId
                )
                        : found.getOwnerUserId() == userId)
                .orElseThrow(() -> new NotFoundException("MEDIA_NOT_FOUND", "Media not found"));
        return createOwnLink(userId, entity, entityId, item.getId(), purpose);
    }

    @Transactional
    public void updateLink(long userId, EntityType entity, long entityId, long linkId, @Nullable String purpose,
                           @Nullable Boolean hidden) {
        MediaLink link = requireVisibleLink(userId, entity, entityId, linkId);
        if (link.getOwnerUserId() != null) {
            if (hidden != null) {
                throw new BadRequestException("MEDIA_NOT_EDITABLE", "Own media is removed, not hidden");
            }
            if (purpose != null) {
                MediaPurposes.requireValid(purpose);
                link.setPurpose(purpose);
                if (MediaPurposes.isSingle(purpose)) {
                    removeOtherOwnLinks(userId, entity, entityId, purpose, link.getId());
                }
            }
        } else {
            if (purpose != null) {
                throw new BadRequestException("MEDIA_NOT_EDITABLE", "Source media keeps its purpose");
            }
            if (hidden != null) {
                MediaLinkOverride override = findOrNewOverride(userId, linkId);
                override.setHidden(hidden);
                saveOrDropOverride(override);
            }
        }
    }

    @Transactional
    public void deleteLink(long userId, EntityType entity, long entityId, long linkId) {
        MediaLink link = requireVisibleLink(userId, entity, entityId, linkId);
        if (link.getOwnerUserId() == null) {
            throw new BadRequestException("MEDIA_NOT_EDITABLE", "Source media is hidden, not removed");
        }
        linkRepository.delete(link);
        linkRepository.flush();
        deleteMediaIfUnreferenced(link.getMediaItemId());
    }

    @Transactional
    public void reorder(long userId, EntityType entity, long entityId, List<Long> linkIds) {
        Map<Long, MediaLink> links = linkRepository.findVisible(entity, entityId, userId).stream()
                .collect(Collectors.toMap(MediaLink::getId, Function.identity()));
        int order = 1;
        for (Long id : linkIds) {
            MediaLink link = links.get(id);
            if (link == null) {
                continue;
            }
            if (link.getOwnerUserId() != null) {
                link.setSortOrder(order++);
            } else {
                MediaLinkOverride override = findOrNewOverride(userId, link.getId());
                override.setSortOrder(order++);
                saveOrDropOverride(override);
            }
        }
    }

    @Transactional
    public void deleteOwned(long userId, EntityType entity, Collection<Long> entityIds) {
        if (entityIds.isEmpty()) {
            return;
        }
        List<Long> sourceLinkIds = linkRepository.findByEntityAndEntityIdInAndOwnerUserIdIsNull(
                entity,
                entityIds
        ).stream().map(MediaLink::getId).toList();
        overrideRepository.deleteByUserIdAndLinkIdIn(userId, sourceLinkIds);
        List<MediaLink> links = linkRepository.findByEntityAndEntityIdInAndOwnerUserId(
                entity, entityIds,
                userId
        );
        linkRepository.deleteAll(links);
        linkRepository.flush();
        links.stream().map(MediaLink::getMediaItemId).distinct().forEach(this::deleteMediaIfUnreferenced);
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> singleSourceMediaItems(EntityType entity, Collection<Long> entityIds, String purpose) {
        if (!MediaPurposes.isSingle(purpose)) {
            throw new IllegalArgumentException("Not a single purpose: " + purpose);
        }
        if (entityIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, Long> result = new HashMap<>();
        for (MediaLink link : linkRepository.findByEntityAndEntityIdInAndPurposeAndOwnerUserIdIsNull(
                entity, entityIds, purpose)) {
            result.putIfAbsent(link.getEntityId(), link.getMediaItemId());
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> singleMediaItems(long userId, EntityType entity, Collection<Long> entityIds,
                                            String purpose) {
        if (!MediaPurposes.isSingle(purpose)) {
            throw new IllegalArgumentException("Not a single purpose: " + purpose);
        }
        if (entityIds.isEmpty()) {
            return Map.of();
        }
        List<MediaLink> links = linkRepository.findVisibleWithPurpose(
                entity, entityIds,
                purpose, userId
        );
        Set<Long> hidden = overrideRepository.findByUserIdAndLinkIdIn(
                        userId, links.stream()
                                .filter(link -> link.getOwnerUserId() == null).map(MediaLink::getId).toList()
                )
                .stream().filter(MediaLinkOverride::isHidden).map(MediaLinkOverride::getLinkId)
                .collect(Collectors.toSet());

        Map<Long, Long> result = new HashMap<>();
        for (MediaLink link : links) {
            if (link.getOwnerUserId() != null) {
                result.put(link.getEntityId(), link.getMediaItemId());
            } else if (!hidden.contains(link.getId())) {
                result.putIfAbsent(link.getEntityId(), link.getMediaItemId());
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<MediaLink> sourceLinks(EntityType entity, long entityId) {
        return linkRepository.findByEntityAndEntityIdAndOwnerUserIdIsNull(entity, entityId);
    }

    @Transactional
    public void createSourceLink(EntityType entity, long entityId, long mediaItemId, String sourceKey,
                                 String purpose, int sortOrder) {
        linkRepository.save(new MediaLink(mediaItemId, entity, entityId, null, sourceKey, purpose, sortOrder));
    }

    @Transactional
    public void updateSourceLink(long linkId, String purpose, int sortOrder) {
        MediaLink link = linkRepository.findById(linkId).orElseThrow();
        link.setPurpose(purpose);
        link.setSortOrder(sortOrder);
    }

    @Transactional
    public void replaceSourceMedia(long linkId, long mediaItemId, String purpose, int sortOrder) {
        MediaLink link = linkRepository.findById(linkId).orElseThrow();
        long oldMediaItemId = link.getMediaItemId();
        link.setMediaItemId(mediaItemId);
        link.setPurpose(purpose);
        link.setSortOrder(sortOrder);
        linkRepository.deleteAll(linkRepository.findByMediaItemIdAndOwnerUserIdIsNotNull(oldMediaItemId));
        linkRepository.flush();
        deleteMediaIfUnreferenced(oldMediaItemId);
    }

    @Transactional
    public void removeSourceLink(long linkId) {
        MediaLink link = linkRepository.findById(linkId).orElseThrow();
        overrideRepository.deleteByLinkId(linkId);
        linkRepository.deleteAll(linkRepository.findByMediaItemIdAndOwnerUserIdIsNotNull(link.getMediaItemId()));
        linkRepository.delete(link);
        linkRepository.flush();
        deleteMediaIfUnreferenced(link.getMediaItemId());
    }

    private void removeOtherOwnLinks(long userId, EntityType entity, long entityId, String purpose, long keptLinkId) {
        List<MediaLink> others = linkRepository.findByEntityAndEntityIdAndOwnerUserIdAndPurpose(
                entity,
                entityId, userId, purpose
        ).stream().filter(link -> link.getId() != keptLinkId).toList();
        linkRepository.deleteAll(others);
        linkRepository.flush();
        others.forEach(link -> deleteMediaIfUnreferenced(link.getMediaItemId()));
    }

    private MediaLink requireVisibleLink(long userId, EntityType entity, long entityId, long linkId) {
        return linkRepository.findVisible(linkId, entity, entityId, userId)
                .orElseThrow(() -> new NotFoundException("MEDIA_NOT_FOUND", "Media not found"));
    }

    private MediaLinkOverride findOrNewOverride(long userId, long linkId) {
        return overrideRepository.findByLinkIdAndUserId(linkId, userId)
                .orElseGet(() -> new MediaLinkOverride(linkId, userId, false, null));
    }

    private void saveOrDropOverride(MediaLinkOverride override) {
        if (!override.isHidden() && override.getSortOrder() == null) {
            overrideRepository.delete(override);
        } else {
            overrideRepository.save(override);
        }
    }

    private void deleteMediaIfUnreferenced(long mediaItemId) {
        if (linkRepository.countByMediaItemId(mediaItemId) == 0) {
            itemService.delete(mediaItemId);
        }
    }
}
