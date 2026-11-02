package notkirill.workshopmanager.item.library;

import notkirill.workshopmanager.content.ContentJobService;
import notkirill.workshopmanager.content.ContentService;
import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.item.lifecycle.ItemLifecycleService;
import notkirill.workshopmanager.item.organization.group.GroupService;
import notkirill.workshopmanager.item.organization.rating.RatingService;
import notkirill.workshopmanager.item.organization.relation.RelationService;
import notkirill.workshopmanager.item.organization.tag.TagService;
import notkirill.workshopmanager.item.personal.ItemMetadataPersonal;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.item.shared.ItemInternalService;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.item.sync.ItemSyncService;
import notkirill.workshopmanager.item.web.dto.UpdateItemRequest;
import notkirill.workshopmanager.media.MediaLinkService;
import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemCommandService {
    private final PersonalItemService personalService;
    private final ItemInternalService internalService;
    private final TagService tagService;
    private final GroupService groupService;
    private final RatingService ratingService;
    private final RelationService relationService;
    private final ItemLifecycleService lifecycleService;
    private final ItemSyncService syncService;
    private final ContentService contentService;
    private final ContentJobService contentJobService;
    private final MediaLinkService mediaLinkService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public ItemCommandService(PersonalItemService personalService, ItemInternalService internalService,
                              TagService tagService, GroupService groupService, RatingService ratingService,
                              RelationService relationService,
                              ItemLifecycleService lifecycleService, ItemSyncService syncService,
                              ContentService contentService, ContentJobService contentJobService,
                              MediaLinkService mediaLinkService, PlatformTransactionManager transactionManager) {
        this.personalService = personalService;
        this.internalService = internalService;
        this.tagService = tagService;
        this.groupService = groupService;
        this.ratingService = ratingService;
        this.relationService = relationService;
        this.lifecycleService = lifecycleService;
        this.syncService = syncService;
        this.contentService = contentService;
        this.contentJobService = contentJobService;
        this.mediaLinkService = mediaLinkService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void update(long userId, long personalId, UpdateItemRequest request) {
        ItemMetadataPersonal personal = personalService.requireOwned(userId, personalId);
        ItemMetadataInternal internal = requireInternal(personal);
        String title = request.title();
        if (internal.getSourceModspace() == ModspaceType.LOCAL && title == null) {
            throw new BadRequestException("TITLE_REQUIRED", "Local items need a title (there is no original)");
        }
        boolean autoBackup = request.autoBackup() && internal.getSourceModspace() != ModspaceType.LOCAL;
        boolean turnedOn = autoBackup && !personal.isAutoBackup();

        personal.setTitle(title);
        personal.setDescription(request.description());
        personal.setUserCommentary(request.userCommentary());
        personal.setAutoBackup(autoBackup);
        personalService.save(personal);

        if (turnedOn) {
            contentJobService.requestBackup(internal.getId(), userId);
        }
    }

    public int setAutoBackup(long userId, Collection<Long> personalIds, boolean autoBackup) {
        List<ItemMetadataPersonal> personals = personalService.requireOwnedAll(userId, personalIds);
        Map<Long, ItemMetadataInternal> internals = internals(personals);
        List<Long> backups = new ArrayList<>();
        for (ItemMetadataPersonal personal : personals) {
            ItemMetadataInternal internal = internals.get(personal.getInternalMetadataId());
            boolean effective = autoBackup && internal != null && internal.getSourceModspace() != ModspaceType.LOCAL;
            if (effective && !personal.isAutoBackup()) {
                backups.add(personal.getInternalMetadataId());
            }
            personal.setAutoBackup(effective);
        }
        personalService.saveAll(personals);
        backups.forEach(internalId -> contentJobService.requestBackup(internalId, userId));
        return personals.size();
    }

    public int unregister(long userId, Collection<Long> personalIds) {
        List<ItemMetadataPersonal> personals = personalService.requireOwnedAll(userId, personalIds);
        List<Long> ids = personals.stream().map(ItemMetadataPersonal::getId).toList();
        Set<Long> internalIds = personals.stream().map(ItemMetadataPersonal::getInternalMetadataId)
                .collect(Collectors.toSet());

        transactionTemplate.executeWithoutResult(status -> {
            groupService.deleteOfItems(ids);
            tagService.deleteOfItems(ids);
            ratingService.deleteOfItems(ids);
            relationService.deleteOfItems(ids);
            mediaLinkService.deleteOwned(userId, EntityType.ITEM, internalIds);
            internalIds.forEach(internalId -> contentService.requestDeleteUploads(internalId, userId));
            personalService.deleteAll(personals);
        });
        internalIds.forEach(lifecycleService::onPersonalRemoved);
        return personals.size();
    }

    public int unregisterAll(long userId) {
        List<Long> personalIds = personalService.findAllOfUser(userId).stream()
                .map(ItemMetadataPersonal::getId).toList();
        return personalIds.isEmpty() ? 0 : unregister(userId, personalIds);
    }

    public int refresh(long userId, Collection<Long> personalIds) {
        List<ItemMetadataPersonal> personals = personalService.requireOwnedAll(userId, personalIds);
        Map<ModspaceType, List<String>> byModspace = new EnumMap<>(ModspaceType.class);
        for (ItemMetadataInternal internal : internals(personals).values()) {
            if (internal.getSourceModspace() != ModspaceType.LOCAL) {
                byModspace.computeIfAbsent(internal.getSourceModspace(), m -> new ArrayList<>())
                        .add(internal.getOriginalId());
            }
        }
        int refreshed = 0;
        for (Map.Entry<ModspaceType, List<String>> entry : byModspace.entrySet()) {
            refreshed += syncService.sync(entry.getKey(), entry.getValue()).size();
        }
        return refreshed;
    }

    private ItemMetadataInternal requireInternal(ItemMetadataPersonal personal) {
        return internalService.find(personal.getInternalMetadataId())
                .orElseThrow(() -> new IllegalStateException("Personal item without Internal identity"));
    }

    private Map<Long, ItemMetadataInternal> internals(List<ItemMetadataPersonal> personals) {
        return internalService.findAll(personals.stream().map(ItemMetadataPersonal::getInternalMetadataId).toList());
    }
}
