package notkirill.workshopmanager.item.library.search;

import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.item.library.search.antlr.ItemSearchGrammarBaseVisitor;
import notkirill.workshopmanager.item.library.search.antlr.ItemSearchGrammarParser;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Locale;
import java.util.regex.Pattern;

public class ItemSearchVisitor extends ItemSearchGrammarBaseVisitor<ItemSearchCriterion> {
    private static final Pattern ESCAPED_CHAR = Pattern.compile("\\\\([\"\\\\])");

    private final ItemSearchPredicateBuilder predicateBuilder;
    private final ItemSearchProperties properties;

    private int currentDepth = 0;
    private Integer size = null;

    private String sortField = null;
    private Boolean sortAsc = null;

    public ItemSearchVisitor(ItemSearchPredicateBuilder predicateBuilder, ItemSearchProperties properties) {
        this.predicateBuilder = predicateBuilder;
        this.properties = properties;
    }

    @Override
    public ItemSearchCriterion visitQuery(ItemSearchGrammarParser.QueryContext ctx) {
        return visit(ctx.or_expr());
    }

    @Override
    public ItemSearchCriterion visitOr_expr(ItemSearchGrammarParser.Or_exprContext ctx) {
        ItemSearchCriterion spec = null;
        for (var andExpr : ctx.and_expr()) {
            ItemSearchCriterion next = visit(andExpr);
            if (next != null) {
                spec = (spec == null) ? next : spec.or(next);
            }
        }
        return spec;
    }

    @Override
    public ItemSearchCriterion visitAnd_expr(ItemSearchGrammarParser.And_exprContext ctx) {
        ItemSearchCriterion spec = null;
        for (var notExpr : ctx.not_expr()) {
            ItemSearchCriterion next = visit(notExpr);
            if (next != null) {
                spec = (spec == null) ? next : spec.and(next);
            }
        }
        return spec;
    }

    @Override
    public ItemSearchCriterion visitNot_expr(ItemSearchGrammarParser.Not_exprContext ctx) {
        ItemSearchCriterion spec = visit(ctx.primary());
        return ctx.NOT() != null ? spec.negate() : spec;
    }

    @Override
    public ItemSearchCriterion visitPrimary(ItemSearchGrammarParser.PrimaryContext ctx) {
        if (ctx.LPAREN() != null) {
            enterDeepLevel();
            ItemSearchCriterion spec = visit(ctx.or_expr());
            currentDepth--;
            return spec;
        }
        return visit(ctx.criteria());
    }

    @Override
    public ItemSearchCriterion visitCriteria(ItemSearchGrammarParser.CriteriaContext ctx) {
        String rawTag = ctx.TAG() != null ? ctx.TAG().getText().replace(":", "") : null;
        String tag = rawTag != null ? rawTag.toLowerCase(Locale.ROOT) : null;
        String value = extractValue(ctx);

        if ("order".equals(tag) || "size".equals(tag)) {
            validateDirective(ctx, tag);

            if ("order".equals(tag)) {
                applySort(value);
            } else {
                applySize(value);
            }
            return null;
        }

        return (root, query, cb) -> tag == null
                ? predicateBuilder.buildDefaultSearch(root, query, cb, value)
                : predicateBuilder.buildTagPredicate(root, query, cb, ctx, tag, value);
    }

    private void validateDirective(ItemSearchGrammarParser.CriteriaContext ctx, String tag) {
        if ("order".equals(tag) && this.sortField != null) {
            throw new BadRequestException(
                    "DUPLICATE_SEARCH_DIRECTIVE", "Directive order: cannot be specified more than once.");
        }
        if ("size".equals(tag) && this.size != null) {
            throw new BadRequestException(
                    "DUPLICATE_SEARCH_DIRECTIVE", "Directive size: cannot be specified more than once.");
        }

        if (isInsideParentheses(ctx)) {
            throw new BadRequestException(
                    "INVALID_SEARCH_DIRECTIVE",
                    "Directives like " + tag + ": cannot be used inside parenthesized expressions."
            );
        }

        if (isNegated(ctx)) {
            throw new BadRequestException(
                    "INVALID_SEARCH_DIRECTIVE",
                    "Directives like " + tag + ": cannot be negated."
            );
        }

        if (isInsideOrExpr(ctx)) {
            throw new BadRequestException(
                    "INVALID_SEARCH_DIRECTIVE",
                    "Directives like " + tag + ": cannot be combined with OR."
            );
        }
    }

    private boolean isInsideParentheses(ParseTree node) {
        int primaryCount = 0;
        while (node != null) {
            if (node instanceof ItemSearchGrammarParser.PrimaryContext) {
                primaryCount++;
                if (primaryCount > 1) {
                    return true;
                }
            }
            node = node.getParent();
        }
        return false;
    }

    private boolean isNegated(ParseTree node) {
        while (node != null) {
            if (node instanceof ItemSearchGrammarParser.Not_exprContext notCtx) {
                return notCtx.NOT() != null;
            }
            node = node.getParent();
        }
        return false;
    }

    private boolean isInsideOrExpr(ParseTree node) {
        while (node != null) {
            if (node instanceof ItemSearchGrammarParser.Or_exprContext orCtx
                    && orCtx.OR() != null && !orCtx.OR().isEmpty()) {
                return true;
            }

            node = node.getParent();
        }
        return false;
    }

    private void applySort(String value) {
        String normalized = value != null ? value.trim().toLowerCase(Locale.ROOT) : "";
        if (normalized.endsWith("_asc")) {
            this.sortAsc = true;
            this.sortField = normalized.substring(0, normalized.length() - 4);
        } else if (normalized.endsWith("_desc")) {
            this.sortAsc = false;
            this.sortField = normalized.substring(0, normalized.length() - 5);
        } else {
            this.sortAsc = null;
            this.sortField = normalized;
        }

        if (this.sortField.isBlank()) {
            throw new BadRequestException("INVALID_SORT_FIELD", "Sort field cannot be blank.");
        }
    }

    private void applySize(String value) {
        try {
            this.size = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new BadRequestException("INVALID_SEARCH_PAGE_SIZE", "Invalid size value: " + value, e);
        }
    }

    private void enterDeepLevel() {
        if (++currentDepth > properties.maxDepth()) {
            throw new BadRequestException(
                    "SEARCH_QUERY_DEPTH_EXCEEDED", "Query complexity exceeds maximum depth.");
        }
    }

    private String extractValue(ItemSearchGrammarParser.CriteriaContext ctx) {
        if (ctx.PHRASE() != null) {
            String text = ctx.PHRASE().getText();
            return ESCAPED_CHAR.matcher(text.substring(1, text.length() - 1)).replaceAll("$1");
        }
        if (ctx.WORD() != null) return ctx.WORD().getText();
        if (ctx.comp_val() != null) return ctx.comp_val().val.getText();
        if (ctx.range_val() != null) return ctx.range_val().getText();
        return "";
    }

    public Integer getSize() {
        return size;
    }

    public String getSortField() {
        return sortField;
    }

    public Boolean getSortAsc() {
        return sortAsc;
    }
}