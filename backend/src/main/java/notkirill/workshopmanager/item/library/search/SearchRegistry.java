package notkirill.workshopmanager.item.library.search;

import notkirill.workshopmanager.item.enums.ItemSourceStatus;
import notkirill.workshopmanager.modspace.ModspaceType;

import java.util.*;

public final class SearchRegistry {
    private static final Map<String, SearchField> REGISTRY;
    private static final List<SearchField> DEFAULT_SEARCH_FIELDS;

    static {
        Map<String, SearchField> registry = new LinkedHashMap<>();
        List<SearchField> defaultSearchFields = new ArrayList<>();

        List<SearchField> fields = List.of(
                SearchField.numeric("id", (r, cb) -> r.personal().get("id"), true),
                SearchField.stringLike(
                        "title", (r, cb) -> cb.coalesce(
                                r.personal().<String>get("title"), r.projection().<String>get("title")), true
                ),
                SearchField.stringLike(
                        "description", (r, cb) -> cb.coalesce(
                                r.personal().<String>get("description"), r.projection().<String>get("description")),
                        true
                ),
                SearchField.stringLike("commentary", (r, cb) -> r.personal().get("userCommentary"), false),
                SearchField.bool("auto_backup", (r, cb) -> r.personal().get("autoBackup")),
                SearchField.numeric("game", (r, cb) -> r.internal().get("gameId"), true),
                SearchField.ofEnum("modspace", (r, cb) -> r.internal().get("sourceModspace"), ModspaceType.class),
                SearchField.exact("original", (r, cb) -> r.internal().get("originalId")),
                SearchField.date("registered", (r, cb) -> r.personal().get("timeRegistered"), true),
                SearchField.date("updated", (r, cb) -> r.internal().get("timeUpdated"), true),
                SearchField.date("created", (r, cb) -> r.projection().get("sourceTimeCreated"), true),
                SearchField.numeric("rating", (r, cb) -> r.rating().get("score"), true),
                SearchField.ofEnum("status", (r, cb) -> r.internal().get("sourceStatus"), ItemSourceStatus.class),
                SearchField.custom("tag", SearchField.TagType.USER_TAG),
                SearchField.custom("group", SearchField.TagType.USER_GROUP),
                SearchField.custom("backup", SearchField.TagType.HAS_BACKUP)
        );

        for (SearchField field : fields) {
            registry.put(field.tag(), field);
            if (field.defaultSearch()) {
                defaultSearchFields.add(field);
            }
        }

        REGISTRY = Collections.unmodifiableMap(registry);
        DEFAULT_SEARCH_FIELDS = Collections.unmodifiableList(defaultSearchFields);
    }

    private SearchRegistry() {

    }

    public static SearchField get(String tag) {
        return REGISTRY.get(tag);
    }

    public static boolean contains(String tag) {
        return REGISTRY.containsKey(tag);
    }

    public static List<SearchField> getDefaultSearchFields() {
        return DEFAULT_SEARCH_FIELDS;
    }

    public static Collection<SearchField> getAll() {
        return REGISTRY.values();
    }
}
