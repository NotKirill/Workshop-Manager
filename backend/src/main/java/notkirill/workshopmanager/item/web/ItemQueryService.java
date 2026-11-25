package notkirill.workshopmanager.item.web;

import notkirill.workshopmanager.common.Texts;
import notkirill.workshopmanager.content.ContentService;
import notkirill.workshopmanager.game.GameService;
import notkirill.workshopmanager.item.library.UserItem;
import notkirill.workshopmanager.item.library.UserItemLoader;
import notkirill.workshopmanager.item.library.search.ItemSearchService;
import notkirill.workshopmanager.item.library.search.SearchField;
import notkirill.workshopmanager.item.library.search.SearchRegistry;
import notkirill.workshopmanager.item.organization.tag.TagService;
import notkirill.workshopmanager.item.organization.tag.UserTag;
import notkirill.workshopmanager.item.organization.web.CollectionMapper;
import notkirill.workshopmanager.item.personal.ItemMetadataPersonal;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.item.web.dto.*;
import notkirill.workshopmanager.media.MediaPurposes;
import notkirill.workshopmanager.media.MediaService;
import notkirill.workshopmanager.media.enums.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ItemQueryService {
    private static final int SNIPPET_LENGTH = 280;

    private final ItemSearchService searchService;
    private final UserItemLoader userItemLoader;
    private final GameService gameService;
    private final TagService tagService;
    private final ContentService contentService;
    private final MediaService mediaService;
    private final CollectionMapper collectionMapper;
    private final SearchRegistry searchRegistry;

    @Autowired
    public ItemQueryService(ItemSearchService searchService, UserItemLoader userItemLoader,
                            GameService gameService, TagService tagService, ContentService contentService,
                            MediaService mediaService, CollectionMapper collectionMapper,
                            SearchRegistry searchRegistry) {
        this.searchService = searchService;
        this.userItemLoader = userItemLoader;
        this.gameService = gameService;
        this.tagService = tagService;
        this.contentService = contentService;
        this.mediaService = mediaService;
        this.collectionMapper = collectionMapper;
        this.searchRegistry = searchRegistry;
    }

    @Transactional(readOnly = true)
    public ItemPageResponse search(long userId, ItemSearchQuery query) {
        Page<ItemMetadataPersonal> page = searchService.search(query.searchQuery(), query.page(), userId);
        return new ItemPageResponse(
                toSummaries(userId, userItemLoader.load(page.getContent())), page.getNumber(),
                page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }

    private List<ItemSummaryResponse> toSummaries(long userId, List<UserItem> items) {
        if (items.isEmpty()) {
            return List.of();
        }
        Map<Long, String> gameNames = gameService.getNames(
                items.stream().map(item -> item.internal().getGameId()).toList());
        Map<Long, List<UserTag>> tags = tagService.getTagsOfItems(
                items.stream().map(UserItem::personalId).toList());
        List<Long> internalIds = items.stream().map(item -> item.internal().getId()).toList();
        Map<Long, ContentService.Brief> backups = contentService.getBriefs(userId, internalIds);
        Map<Long, String> previews = mediaService.singleImageUrls(
                userId, EntityType.ITEM, internalIds,
                MediaPurposes.PREVIEW
        );

        List<ItemSummaryResponse> result = new ArrayList<>();
        for (UserItem item : items) {
            ItemMetadataInternal internal = item.internal();
            ContentService.Brief brief = backups.get(internal.getId());
            result.add(new ItemSummaryResponse(
                    item.personalId(),
                    internal.getId(),
                    item.title(),
                    Texts.truncate(item.description(), SNIPPET_LENGTH),
                    item.titleOverridden(),
                    item.descriptionOverridden(),
                    internal.getSourceModspace(),
                    internal.getOriginalId(),
                    new NamedRef(internal.getGameId(), gameNames.get(internal.getGameId())),
                    item.personal().isAutoBackup(),
                    item.rating(),
                    item.sourceStatus(),
                    item.fileSize(),
                    item.sourceTimeUpdated(),
                    item.personal().getTimeRegistered(),
                    collectionMapper.tagRefs(tags.getOrDefault(item.personalId(), List.of())),
                    brief == null ? null : new ItemSummaryResponse.Backup(
                            brief.revisionCount(),
                            brief.latestBackupAt(), brief.activeJob()
                    ),
                    previews.get(internal.getId())
            ));
        }
        return result;
    }

    public SearchHelpResponse getSearchHelp() {
        List<SearchHelpResponse.Field> fields = new ArrayList<>();
        for (SearchField field : searchRegistry.getAll()) {
            List<String> values = field.enumClass() == null ? List.of()
                    : Arrays.stream(field.enumClass().getEnumConstants()).map(Enum::name).toList();
            if (field.type() == SearchField.TagType.HAS_BACKUP || field.type() == SearchField.TagType.BOOLEAN) {
                values = List.of("yes", "no");
            }
            fields.add(new SearchHelpResponse.Field(field.tag(), field.type().name(), field.sortable(), values));
        }
        return new SearchHelpResponse(
                fields, List.of(
                "skin pack",
                "title:\"exact phrase\" -modspace:local",
                "game:4 rating:>=7 order:rating",
                "registered:2026-01..2026-03 tag:maps",
                "updated:>month status:unavailable",
                "status:hidden OR status:unavailable",
                "steam.tag:maps steam.subscriptions:>10000",
                "tag:maps order:title",
                "(group:server OR tag:favorite) backup:no size:50",
                "registered:3_days_ago order:registered_asc"
        )
        );
    }
}
