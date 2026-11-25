package notkirill.workshopmanager.item.library.search;

import jakarta.persistence.criteria.*;
import notkirill.workshopmanager.content.models.ItemContent;
import notkirill.workshopmanager.content.models.ItemContentRevision;
import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.item.library.search.antlr.ItemSearchGrammarLexer;
import notkirill.workshopmanager.item.library.search.antlr.ItemSearchGrammarParser.CriteriaContext;
import notkirill.workshopmanager.item.organization.group.UserGroup;
import notkirill.workshopmanager.item.organization.group.UserGroupItem;
import notkirill.workshopmanager.item.organization.tag.UserItemTag;
import notkirill.workshopmanager.item.organization.tag.UserTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class ItemSearchPredicateBuilder {
    private final DateRangeParser dateRangeParser;
    private final SearchRegistry searchRegistry;

    @Autowired
    public ItemSearchPredicateBuilder(DateRangeParser dateRangeParser, SearchRegistry searchRegistry) {
        this.dateRangeParser = dateRangeParser;
        this.searchRegistry = searchRegistry;
    }

    private static Number toPathType(Expression<? extends Number> path, long value) {
        Class<?> type = path.getJavaType();
        if (type == Short.class || type == short.class) {
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                throw new NumberFormatException("Out of range: " + value);
            }
            return (short) value;
        }
        if (type == Integer.class || type == int.class) {
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                throw new NumberFormatException("Out of range: " + value);
            }
            return (int) value;
        }
        return value;
    }

    private static boolean parseBoolean(SearchField field, String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1" -> true;
            case "false", "no", "0" -> false;
            default -> throw new BadRequestException(
                    "INVALID_BOOLEAN_VALUE", "Expected yes/no for " + field.tag() + ": " + value);
        };
    }

    public Predicate buildDefaultSearch(ItemSearchRoots roots, CriteriaQuery<?> query,
                                        CriteriaBuilder cb, String value) {
        List<SearchField> defaultFields = searchRegistry.getDefaultSearchFields();
        Predicate[] predicates = new Predicate[defaultFields.size()];

        for (int i = 0; i < defaultFields.size(); i++) {
            predicates[i] = buildPredicateForField(roots, query, cb, null, defaultFields.get(i), value);
        }

        return cb.or(predicates);
    }

    public Predicate buildTagPredicate(ItemSearchRoots roots, CriteriaQuery<?> query,
                                       CriteriaBuilder cb, CriteriaContext ctx, String tag, String value) {
        SearchField field = searchRegistry.get(tag);
        if (field == null) {
            throw new BadRequestException("UNSUPPORTED_SEARCH_METATAG", "Unsupported search metatag: " + tag);
        }

        return buildPredicateForField(roots, query, cb, ctx, field, value);
    }

    public void applySorting(ItemSearchRoots roots, CriteriaQuery<?> query, CriteriaBuilder cb,
                             String tag, Boolean asc) {
        if (query.getResultType() != null && Number.class.isAssignableFrom(query.getResultType())) {
            return;
        }

        // TODO hardcoded 'id', should I do something?
        Path<Long> personalId = roots.personal().get("id");
        if (tag == null || tag.isBlank()) {
            query.orderBy(cb.desc(personalId));
            return;
        }

        SearchField field = searchRegistry.get(tag);
        if (field == null || !field.sortable()) {
            throw new BadRequestException("UNSORTABLE_SEARCH_FIELD", "Field is not sortable: " + tag);
        }

        boolean isAsc = asc != null ? asc : field.type() == SearchField.TagType.STRING_LIKE;
        Expression<?> sortExpression = field.target().resolve(roots, cb);
        Order primaryOrder = isAsc ? cb.asc(sortExpression) : cb.desc(sortExpression);

        if ("id".equals(field.tag())) {
            query.orderBy(primaryOrder);
        } else {
            Order nullsLastOrder = cb.asc(
                    cb.selectCase()
                            .when(cb.isNull(sortExpression), 1)
                            .otherwise(0)
            );

            Order tieBreaker = isAsc ? cb.asc(personalId) : cb.desc(personalId);
            query.orderBy(nullsLastOrder, primaryOrder, tieBreaker);
        }
    }

    private Predicate buildPredicateForField(ItemSearchRoots roots, CriteriaQuery<?> query,
                                             CriteriaBuilder cb, CriteriaContext ctx, SearchField field, String value) {
        if (field.sourceTarget() != null) {
            return buildSourcePredicate(roots, query, cb, ctx, field, value);
        }
        return switch (field.type()) {
            case USER_TAG -> buildUserTagPredicate(roots, query, cb, value);
            case USER_GROUP -> buildUserGroupPredicate(roots, query, cb, value);
            case HAS_BACKUP -> buildHasBackupPredicate(roots, query, cb, field, value);
            default -> resolveTypePredicate(field.target().resolve(roots, cb), cb, ctx, field, value);
        };
    }

    private Predicate buildSourcePredicate(ItemSearchRoots roots, CriteriaQuery<?> query, CriteriaBuilder cb,
                                           CriteriaContext ctx, SearchField field, String value) {
        Subquery<Integer> subquery = query.subquery(Integer.class);
        List<Predicate> conditions = new ArrayList<>();
        Expression<?> path = field.sourceTarget().resolve(
                subquery, roots.personal().get("internalMetadataId"), cb, conditions);
        conditions.add(resolveTypePredicate(path, cb, ctx, field, value));
        subquery.select(cb.literal(1)).where(conditions.toArray(Predicate[]::new));
        return cb.exists(subquery);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate resolveTypePredicate(Expression<?> path, CriteriaBuilder cb, CriteriaContext ctx,
                                           SearchField field, String value) {
        return switch (field.type()) {
            case STRING_LIKE -> cb.like(
                    cb.lower(path.as(String.class)), "%" + escapeLike(value.toLowerCase(Locale.ROOT)) + "%",
                    '\\'
            );
            case ENUM -> {
                try {
                    Enum enumVal = Enum.valueOf(
                            (Class<Enum>) (Class<?>) field.enumClass(), value.toUpperCase(Locale.ROOT));
                    yield cb.equal(
                            path, enumVal instanceof SearchField.StoredValue stored
                                    ? stored.storedValue() : enumVal
                    );
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException(
                            "INVALID_ENUM_VALUE", "Unknown value for " + field.tag() + ": " + value);
                }
            }
            case NUMERIC -> handleNumericRange((Expression<? extends Number>) path, cb, ctx, value);
            case DATE -> handleDateRange(path.as(OffsetDateTime.class), cb, ctx, value);
            case EXACT -> cb.equal(path, value);
            case BOOLEAN -> cb.equal(path, parseBoolean(field, value));
            case USER_TAG, USER_GROUP, HAS_BACKUP -> throw new IllegalStateException("Handled before path resolution");
        };
    }

    private Predicate handleNumericRange(Expression<? extends Number> path, CriteriaBuilder cb, CriteriaContext ctx,
                                         String value) {
        try {
            if (ctx != null && ctx.comp_val() != null) {
                var compCtx = ctx.comp_val();
                Number val = toPathType(path, Long.parseLong(compCtx.val.getText()));

                return switch (compCtx.op.getType()) {
                    case ItemSearchGrammarLexer.GT -> cb.gt(path, val);
                    case ItemSearchGrammarLexer.GTE -> cb.ge(path, val);
                    case ItemSearchGrammarLexer.LT -> cb.lt(path, val);
                    case ItemSearchGrammarLexer.LTE -> cb.le(path, val);
                    case ItemSearchGrammarLexer.EQ -> cb.equal(path, val);
                    default -> throw new BadRequestException(
                            "UNKNOWN_SEARCH_OPERATOR",
                            "Unknown comparison operator: " + compCtx.op.getText()
                    );
                };
            }

            if (ctx != null && ctx.range_val() != null) {
                var rangeCtx = ctx.range_val();
                Number start = rangeCtx.start != null ? toPathType(
                        path, Long.parseLong(rangeCtx.start.getText())) : null;
                Number end = rangeCtx.end != null ? toPathType(path, Long.parseLong(rangeCtx.end.getText())) : null;

                if (start != null && end != null) return cb.and(cb.ge(path, start), cb.le(path, end));
                if (start != null) return cb.ge(path, start);
                if (end != null) return cb.le(path, end);
            }

            return cb.equal(path, toPathType(path, Long.parseLong(value)));
        } catch (NumberFormatException e) {
            throw new BadRequestException("INVALID_NUMERIC_VALUE", "Invalid number value: " + value, e);
        }
    }

    private Predicate handleDateRange(Expression<? extends OffsetDateTime> datePath, CriteriaBuilder cb,
                                      CriteriaContext ctx, String value) {
        try {
            if (ctx != null && ctx.comp_val() != null) {
                var compCtx = ctx.comp_val();
                DateRangeParser.DateRange range = dateRangeParser.parse(compCtx.val.getText());

                if (range.isRolling()) {
                    return switch (compCtx.op.getType()) {
                        case ItemSearchGrammarLexer.GT -> cb.greaterThan(datePath, range.start());
                        case ItemSearchGrammarLexer.GTE -> cb.greaterThanOrEqualTo(datePath, range.start());
                        case ItemSearchGrammarLexer.LT -> cb.lessThan(datePath, range.start());
                        case ItemSearchGrammarLexer.LTE -> cb.lessThanOrEqualTo(datePath, range.start());
                        case ItemSearchGrammarLexer.EQ -> cb.and(
                                cb.greaterThanOrEqualTo(datePath, range.start()),
                                cb.lessThanOrEqualTo(datePath, range.end())
                        );
                        default -> throw new BadRequestException(
                                "UNKNOWN_SEARCH_OPERATOR",
                                "Unknown comparison operator: " + compCtx.op.getText()
                        );
                    };
                }

                return switch (compCtx.op.getType()) {
                    case ItemSearchGrammarLexer.GT -> cb.greaterThanOrEqualTo(datePath, range.end());
                    case ItemSearchGrammarLexer.GTE -> cb.greaterThanOrEqualTo(datePath, range.start());
                    case ItemSearchGrammarLexer.LT -> cb.lessThan(datePath, range.start());
                    case ItemSearchGrammarLexer.LTE -> cb.lessThan(datePath, range.end());
                    case ItemSearchGrammarLexer.EQ -> cb.and(
                            cb.greaterThanOrEqualTo(datePath, range.start()),
                            cb.lessThan(datePath, range.end())
                    );
                    default -> throw new BadRequestException(
                            "UNKNOWN_SEARCH_OPERATOR",
                            "Unknown comparison operator: " + compCtx.op.getText()
                    );
                };
            }
            if (ctx != null && ctx.range_val() != null) {
                var rangeCtx = ctx.range_val();

                DateRangeParser.DateRange startRange = rangeCtx.start != null
                        ? dateRangeParser.parse(rangeCtx.start.getText())
                        : null;

                DateRangeParser.DateRange endRange = rangeCtx.end != null
                        ? dateRangeParser.parse(rangeCtx.end.getText())
                        : null;

                OffsetDateTime start = startRange != null ? startRange.start() : null;
                OffsetDateTime end = endRange != null ? endRange.end() : null;

                if (start != null && end != null) {
                    if (start.isAfter(end)) {
                        throw new BadRequestException(
                                "INVALID_SEARCH_DATE",
                                "Start date cannot be after end date: " + value
                        );
                    }
                    return cb.and(
                            cb.greaterThanOrEqualTo(datePath, start),
                            cb.lessThan(datePath, end)
                    );
                }
                if (start != null) return cb.greaterThanOrEqualTo(datePath, start);
                if (end != null) return cb.lessThan(datePath, end);

                throw new BadRequestException(
                        "INVALID_SEARCH_DATE", "Range requires at least one bound: " + value);
            }

            DateRangeParser.DateRange range = dateRangeParser.parse(value);
            if (range.isRolling()) {
                return cb.and(
                        cb.greaterThanOrEqualTo(datePath, range.start()),
                        cb.lessThanOrEqualTo(datePath, range.end())
                );
            }
            return cb.and(
                    cb.greaterThanOrEqualTo(datePath, range.start()),
                    cb.lessThan(datePath, range.end())
            );
        } catch (DateTimeException | NumberFormatException e) {
            throw new BadRequestException("INVALID_DATE_VALUE", "Invalid date value: " + value, e);
        }
    }

    private Predicate buildUserTagPredicate(ItemSearchRoots roots, CriteriaQuery<?> query, CriteriaBuilder cb,
                                            String value) {
        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<UserItemTag> itemTag = subquery.from(UserItemTag.class);
        Root<UserTag> tag = subquery.from(UserTag.class);
        subquery.select(cb.literal(1)).where(
                cb.equal(itemTag.get("itemPersonalMetadataId"), roots.personal().get("id")),
                cb.equal(itemTag.get("tagId"), tag.get("id")),
                cb.equal(cb.lower(tag.get("displayName")), value.toLowerCase(Locale.ROOT))
        );
        return cb.exists(subquery);
    }

    private Predicate buildUserGroupPredicate(ItemSearchRoots roots, CriteriaQuery<?> query, CriteriaBuilder cb,
                                              String value) {
        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<UserGroupItem> groupItem = subquery.from(UserGroupItem.class);
        Root<UserGroup> group = subquery.from(UserGroup.class);
        subquery.select(cb.literal(1)).where(
                cb.equal(groupItem.get("itemPersonalMetadataId"), roots.personal().get("id")),
                cb.equal(groupItem.get("userGroupId"), group.get("id")),
                cb.equal(cb.lower(group.get("name")), value.toLowerCase(Locale.ROOT))
        );
        return cb.exists(subquery);
    }

    private Predicate buildHasBackupPredicate(ItemSearchRoots roots, CriteriaQuery<?> query, CriteriaBuilder cb,
                                              SearchField field, String value) {
        boolean expected = parseBoolean(field, value);
        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<ItemContent> content = subquery.from(ItemContent.class);
        Root<ItemContentRevision> revision = subquery.from(ItemContentRevision.class);
        subquery.select(cb.literal(1)).where(
                cb.equal(content.get("internalMetadataId"), roots.personal().get("internalMetadataId")),
                cb.equal(revision.get("itemContentId"), content.get("id")),
                cb.or(
                        cb.isNull(revision.get("uploadedByUserId")),
                        cb.equal(revision.get("uploadedByUserId"), roots.personal().get("userId"))
                )
        );
        return expected ? cb.exists(subquery) : cb.not(cb.exists(subquery));
    }

    private String escapeLike(String value) {
        return value.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}