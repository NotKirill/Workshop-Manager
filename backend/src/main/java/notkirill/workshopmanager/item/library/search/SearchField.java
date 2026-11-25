package notkirill.workshopmanager.item.library.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;

import java.util.List;

public record SearchField(
        String tag,
        Target target,
        SourceTarget sourceTarget,
        TagType type,
        Class<? extends Enum<?>> enumClass,
        boolean sortable,
        boolean defaultSearch
) {
    public static SearchField numeric(String tag, Target target, boolean sortable) {
        return new SearchField(tag, target, null, TagType.NUMERIC, null, sortable, false);
    }

    public static SearchField date(String tag, Target target, boolean sortable) {
        return new SearchField(tag, target, null, TagType.DATE, null, sortable, false);
    }

    public static SearchField stringLike(String tag, Target target, boolean sortable, boolean defaultSearch) {
        return new SearchField(tag, target, null, TagType.STRING_LIKE, null, sortable, defaultSearch);
    }

    public static SearchField ofEnum(String tag, Target target, Class<? extends Enum<?>> enumClass) {
        return new SearchField(tag, target, null, TagType.ENUM, enumClass, false, false);
    }

    public static SearchField bool(String tag, Target target) {
        return new SearchField(tag, target, null, TagType.BOOLEAN, null, false, false);
    }

    public static SearchField exact(String tag, Target target) {
        return new SearchField(tag, target, null, TagType.EXACT, null, false, false);
    }

    public static SearchField custom(String tag, TagType type) {
        return new SearchField(tag, null, null, type, null, false, false);
    }

    public static SearchField source(String tag, TagType type, Class<? extends Enum<?>> enumClass,
                                     SourceTarget sourceTarget) {
        return new SearchField(tag, null, sourceTarget, type, enumClass, false, false);
    }

    public SearchField withPrefix(String prefix) {
        return new SearchField(prefix + tag, target, sourceTarget, type, enumClass, sortable, defaultSearch);
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

    @FunctionalInterface
    public interface SourceTarget {
        Expression<?> resolve(Subquery<Integer> subquery, Expression<Long> internalId, CriteriaBuilder cb,
                              List<Predicate> conditions);
    }

    public interface StoredValue {
        Object storedValue();
    }
}
