package notkirill.workshopmanager.item.library.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;

public record SearchField(
        String tag,
        Target target,
        TagType type,
        Class<? extends Enum<?>> enumClass,
        boolean sortable,
        boolean defaultSearch
) {
    public static SearchField numeric(String tag, Target target, boolean sortable) {
        return new SearchField(tag, target, TagType.NUMERIC, null, sortable, false);
    }

    public static SearchField date(String tag, Target target, boolean sortable) {
        return new SearchField(tag, target, TagType.DATE, null, sortable, false);
    }

    public static SearchField stringLike(String tag, Target target, boolean defaultSearch) {
        return new SearchField(tag, target, TagType.STRING_LIKE, null, false, defaultSearch);
    }

    public static SearchField ofEnum(String tag, Target target, Class<? extends Enum<?>> enumClass) {
        return new SearchField(tag, target, TagType.ENUM, enumClass, false, false);
    }

    public static SearchField bool(String tag, Target target) {
        return new SearchField(tag, target, TagType.BOOLEAN, null, false, false);
    }

    public static SearchField exact(String tag, Target target) {
        return new SearchField(tag, target, TagType.EXACT, null, false, false);
    }

    public static SearchField custom(String tag, TagType type) {
        return new SearchField(tag, null, type, null, false, false);
    }

    public enum TagType {
        NUMERIC,
        DATE,
        STRING_LIKE,
        ENUM,
        EXACT,
        BOOLEAN,
        USER_TAG,
        USER_GROUP,
        HAS_BACKUP
    }

    @FunctionalInterface
    public interface Target {
        Expression<?> resolve(ItemSearchRoots roots, CriteriaBuilder cb);
    }
}
