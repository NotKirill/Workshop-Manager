package notkirill.workshopmanager.item.library.search;

import notkirill.workshopmanager.item.enums.ItemSourceStatus;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SearchRegistry {
    private static final List<SearchField> CORE_FIELDS = List.of(
            SearchField.numeric("id", (r, cb) -> r.personal().get("id"), true),
            SearchField.stringLike(
                    "title", (r, cb) -> cb.coalesce(
                            r.personal().<String>get("title"), r.projection().<String>get("title")), true, true
            ),
            SearchField.stringLike(
                    "description", (r, cb) -> cb.coalesce(
                            r.personal().<String>get("description"), r.projection().<String>get("description")),
                    false, true
            ),
            SearchField.stringLike("commentary", (r, cb) -> r.personal().get("userCommentary"), false, false),
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

    private final Map<String, SearchField> fields;
    private final List<SearchField> defaultSearchFields;

    @Autowired
    public SearchRegistry(List<ModspaceSearchFields> modspaceFields) {
        Map<String, SearchField> registry = new LinkedHashMap<>();
        CORE_FIELDS.forEach(field -> register(registry, field));
        for (ModspaceSearchFields modspace : modspaceFields) {
            String prefix = modspace.modspace().name().toLowerCase(Locale.ROOT) + ".";
            modspace.fields().forEach(field -> register(registry, field.withPrefix(prefix)));
        }
        this.fields = Collections.unmodifiableMap(registry);
        this.defaultSearchFields = registry.values().stream().filter(SearchField::defaultSearch).toList();
    }

    private static void register(Map<String, SearchField> registry, SearchField field) {
        if (registry.putIfAbsent(field.tag(), field) != null) {
            throw new IllegalStateException("Search field registered twice: " + field.tag());
        }
    }

    public SearchField get(String tag) {
        return fields.get(tag);
    }

    public List<SearchField> getDefaultSearchFields() {
        return defaultSearchFields;
    }

    public Collection<SearchField> getAll() {
        return fields.values();
    }
}
