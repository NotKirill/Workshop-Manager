package notkirill.workshopmanager.item.web;

import notkirill.workshopmanager.author.AuthorMetadata;
import notkirill.workshopmanager.author.AuthorService;
import notkirill.workshopmanager.content.ContentService;
import notkirill.workshopmanager.content.web.ContentResponseMapper;
import notkirill.workshopmanager.content.worker.ContentJobProcessor;
import notkirill.workshopmanager.exception.NotFoundException;
import notkirill.workshopmanager.game.GameService;
import notkirill.workshopmanager.game.models.GameMetadata;
import notkirill.workshopmanager.item.library.UserItem;
import notkirill.workshopmanager.item.library.UserItemLoader;
import notkirill.workshopmanager.item.organization.group.GroupService;
import notkirill.workshopmanager.item.organization.relation.RelationService;
import notkirill.workshopmanager.item.organization.relation.UserItemRelation;
import notkirill.workshopmanager.item.organization.tag.TagService;
import notkirill.workshopmanager.item.organization.web.CollectionMapper;
import notkirill.workshopmanager.item.personal.ItemMetadataPersonal;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.item.shared.ItemInternalService;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.item.shared.ItemMetadataProjection;
import notkirill.workshopmanager.item.shared.ItemProjectionService;
import notkirill.workshopmanager.item.web.dto.ItemDetailResponse;
import notkirill.workshopmanager.media.MediaService;
import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.media.web.MediaResponseMapper;
import notkirill.workshopmanager.modspace.ModspaceAdapter;
import notkirill.workshopmanager.modspace.ModspaceRegistry;
import notkirill.workshopmanager.modspace.ModspaceType;
import notkirill.workshopmanager.modspace.steam.SteamItemReaderService;
import notkirill.workshopmanager.modspace.steam.web.SteamDetailsMapper;
import notkirill.workshopmanager.modspace.steam.web.SteamDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemDetailService {
    private final UserItemLoader userItemLoader;
    private final ItemInternalService internalService;
    private final ItemProjectionService projectionService;
    private final GameService gameService;
    private final AuthorService authorService;
    private final TagService tagService;
    private final GroupService groupService;
    private final RelationService relationService;
    private final ContentService contentService;
    private final ContentJobProcessor contentJobProcessor;
    private final MediaService mediaService;
    private final ModspaceRegistry modspaceRegistry;
    private final SteamItemReaderService steamItemReaderService;
    private final SteamDetailsMapper steamDetailsMapper;
    private final PersonalItemService personalService;
    private final CollectionMapper collectionMapper;
    private final ContentResponseMapper contentResponseMapper;
    private final MediaResponseMapper mediaResponseMapper;

    @Autowired
    public ItemDetailService(UserItemLoader userItemLoader, ItemInternalService internalService,
                             ItemProjectionService projectionService, GameService gameService,
                             AuthorService authorService, TagService tagService,
                             GroupService groupService, RelationService relationService,
                             ContentService contentService, ContentJobProcessor contentJobProcessor,
                             MediaService mediaService, ModspaceRegistry modspaceRegistry,
                             SteamItemReaderService steamItemReaderService, SteamDetailsMapper steamDetailsMapper,
                             PersonalItemService personalService, CollectionMapper collectionMapper,
                             ContentResponseMapper contentResponseMapper, MediaResponseMapper mediaResponseMapper) {
        this.userItemLoader = userItemLoader;
        this.internalService = internalService;
        this.projectionService = projectionService;
        this.gameService = gameService;
        this.authorService = authorService;
        this.tagService = tagService;
        this.groupService = groupService;
        this.relationService = relationService;
        this.contentService = contentService;
        this.contentJobProcessor = contentJobProcessor;
        this.mediaService = mediaService;
        this.modspaceRegistry = modspaceRegistry;
        this.steamItemReaderService = steamItemReaderService;
        this.steamDetailsMapper = steamDetailsMapper;
        this.personalService = personalService;
        this.collectionMapper = collectionMapper;
        this.contentResponseMapper = contentResponseMapper;
        this.mediaResponseMapper = mediaResponseMapper;
    }

    @Transactional(readOnly = true)
    public ItemDetailResponse getDetail(long userId, long personalId, boolean canManageContent) {
        UserItem item = userItemLoader.find(userId, personalId)
                .orElseThrow(() -> new NotFoundException("ITEM_NOT_FOUND", "Item not found"));
        ItemMetadataPersonal personal = item.personal();
        ItemMetadataInternal internal = item.internal();
        Optional<ItemMetadataProjection> projection = Optional.ofNullable(item.projection());
        GameMetadata game = gameService.requireGame(internal.getGameId());

        ItemDetailResponse.Author author = projection.map(ItemMetadataProjection::getAuthorId)
                .flatMap(authorService::find)
                .map(this::toAuthor)
                .orElse(null);

        boolean external = internal.getSourceModspace() != ModspaceType.LOCAL;
        SteamDetailsResponse steam = internal.getSourceModspace() == ModspaceType.STEAM
                ? steamItemReaderService.findStoredItem(internal.getOriginalId())
                .map(steamDetailsMapper::toResponse)
                .orElse(null)
                : null;
        ItemDetailResponse.Content content = new ItemDetailResponse.Content(
                contentResponseMapper.toSummary(contentService.getSummary(internal.getId(), userId)),
                external && contentJobProcessor.canFetch(internal.getSourceModspace()),
                true,
                canManageContent || personalService.isOnlyUser(internal.getId())
        );

        return new ItemDetailResponse(
                personalId,
                internal.getId(),
                internal.getSourceModspace(),
                internal.getOriginalId(),
                item.sourceStatus(),
                item.title(),
                item.description(),
                item.titleOverridden(),
                item.descriptionOverridden(),
                projection.map(p -> new ItemDetailResponse.Original(p.getTitle(), p.getDescription())).orElse(null),
                new ItemDetailResponse.Personal(
                        personal.getTitle(), personal.getDescription(),
                        personal.getUserCommentary(), personal.isAutoBackup(), personal.getTimeRegistered(),
                        personal.getDependenciesNeedReview()
                ),
                new ItemDetailResponse.Game(game.getId(), game.getName()),
                author,
                item.fileSize(),
                item.sourceTimeCreated(),
                item.sourceTimeUpdated(),
                internal.getTimeSynced(),
                item.rating(),
                collectionMapper.tagRefs(tagService.getTagsOfItems(List.of(personalId))
                                                 .getOrDefault(personalId, List.of())),
                collectionMapper.groupRefs(groupService.getGroupsOfItem(personalId)),
                getRelations(userId, personalId),
                getDependencies(userId, internal),
                steam,
                content,
                mediaService.list(userId, EntityType.ITEM, internal.getId()).stream()
                        .map(mediaResponseMapper::toResponse)
                        .toList()
        );
    }

    private List<ItemDetailResponse.Dependency> getDependencies(long userId, ItemMetadataInternal internal) {
        Optional<ModspaceAdapter> adapter = modspaceRegistry.find(internal.getSourceModspace());
        if (adapter.isEmpty()) {
            return List.of();
        }
        List<String> ids = adapter.get().getDependencies(internal.getOriginalId());
        if (ids.isEmpty()) {
            return List.of();
        }
        Map<String, ItemMetadataInternal> known = new HashMap<>();
        for (String id : ids) {
            internalService.find(internal.getSourceModspace(), id).ifPresent(i -> known.put(id, i));
        }
        Map<Long, ItemMetadataPersonal> owned = personalService.findOfInternals(
                        userId,
                        known.values().stream().map(ItemMetadataInternal::getId).toList()
                ).stream()
                .collect(Collectors.toMap(ItemMetadataPersonal::getInternalMetadataId, Function.identity()));
        Map<Long, ItemMetadataProjection> projections = projectionService.findAll(
                known.values().stream().map(ItemMetadataInternal::getId).toList());

        List<ItemDetailResponse.Dependency> result = new ArrayList<>();
        for (String id : ids) {
            ItemMetadataInternal dependency = known.get(id);
            ItemMetadataPersonal personal = dependency != null ? owned.get(dependency.getId()) : null;
            String title = null;
            if (personal != null && personal.getTitle() != null) {
                title = personal.getTitle();
            } else if (dependency != null && projections.containsKey(dependency.getId())) {
                title = projections.get(dependency.getId()).getTitle();
            }
            result.add(new ItemDetailResponse.Dependency(id, personal != null ? personal.getId() : null, title));
        }
        return result;
    }

    private List<ItemDetailResponse.Relation> getRelations(long userId, long personalId) {
        List<UserItemRelation> outgoing = relationService.findOutgoing(personalId);
        List<UserItemRelation> incoming = relationService.findIncoming(personalId);
        Set<Long> otherIds = new HashSet<>();
        outgoing.forEach(r -> otherIds.add(r.getTargetItemPersonalMetadataId()));
        incoming.forEach(r -> otherIds.add(r.getSourceItemPersonalMetadataId()));
        Map<Long, UserItem> others = userItemLoader.findAll(userId, otherIds).stream()
                .collect(Collectors.toMap(UserItem::personalId, Function.identity()));

        List<ItemDetailResponse.Relation> result = new ArrayList<>();
        for (UserItemRelation relation : outgoing) {
            UserItem other = others.get(relation.getTargetItemPersonalMetadataId());
            if (other != null) {
                result.add(new ItemDetailResponse.Relation(
                        other.personalId(), other.title(), relation.getRelation(),
                        "OUTGOING"
                ));
            }
        }
        for (UserItemRelation relation : incoming) {
            UserItem other = others.get(relation.getSourceItemPersonalMetadataId());
            if (other != null) {
                result.add(new ItemDetailResponse.Relation(
                        other.personalId(), other.title(), relation.getRelation(),
                        "INCOMING"
                ));
            }
        }
        return result;
    }

    private ItemDetailResponse.Author toAuthor(AuthorMetadata author) {
        return new ItemDetailResponse.Author(author.getId(), author.getName());
    }
}
