package notkirill.workshopmanager.item.library.search;

import jakarta.persistence.criteria.Predicate;
import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.item.library.search.antlr.ItemSearchGrammarLexer;
import notkirill.workshopmanager.item.library.search.antlr.ItemSearchGrammarParser;
import notkirill.workshopmanager.item.personal.ItemMetadataPersonal;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import org.antlr.v4.runtime.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemSearchService {
    private final PersonalItemService personalService;
    private final ItemSearchPredicateBuilder itemSearchPredicateBuilder;
    private final ItemSearchProperties properties;

    @Autowired
    public ItemSearchService(PersonalItemService personalService,
                             ItemSearchPredicateBuilder itemSearchPredicateBuilder, ItemSearchProperties properties) {
        this.personalService = personalService;
        this.itemSearchPredicateBuilder = itemSearchPredicateBuilder;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public Page<ItemMetadataPersonal> search(String rawQuery, int page, long userId) {
        page = Math.max(page, 0);

        if (rawQuery == null || rawQuery.trim().isEmpty()) {
            return personalService.search(
                    toSpecification(userId, null, null, false),
                    PageRequest.of(page, properties.defaultPageSize(), Sort.unsorted())
            );
        }

        validateRawQuery(rawQuery);

        ItemSearchGrammarLexer lexer = new ItemSearchGrammarLexer(CharStreams.fromString(rawQuery));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new BadRequestException(
                        "UNKNOWN_SEARCH_SYMBOL", "Invalid character at position " + charPositionInLine);
            }
        });

        ItemSearchGrammarParser parser = new ItemSearchGrammarParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.setErrorHandler(new SearchQueryErrorStrategy());

        ItemSearchVisitor visitor = new ItemSearchVisitor(itemSearchPredicateBuilder, properties);
        ItemSearchCriterion criterion = visitor.visit(parser.query());

        int pageSize = visitor.getSize() != null
                ? Math.min(visitor.getSize(), properties.maxPageSize())
                : properties.defaultPageSize();
        validatePageSize(pageSize);

        Pageable pageable = PageRequest.of(page, pageSize, Sort.unsorted());
        return personalService.search(
                toSpecification(userId, criterion, visitor.getSortField(), visitor.getSortAsc()), pageable);
    }

    private Specification<ItemMetadataPersonal> toSpecification(long userId, ItemSearchCriterion criterion,
                                                                String sortField, Boolean sortAsc) {
        return (root, query, cb) -> {
            ItemSearchRoots roots = ItemSearchRoots.join(root, cb);
            itemSearchPredicateBuilder.applySorting(roots, query, cb, sortField, sortAsc);
            Predicate ownItems = cb.equal(root.get("userId"), userId);
            return criterion == null ? ownItems : cb.and(ownItems, criterion.toPredicate(roots, query, cb));
        };
    }

    private void validateRawQuery(String rawQuery) {
        if (rawQuery.length() > properties.maxQueryLength()) {
            throw new BadRequestException(
                    "SEARCH_QUERY_TOO_LONG", "Search query exceeds maximum length of " + properties.maxQueryLength());
        }
    }

    private void validatePageSize(int pageSize) {
        if (pageSize <= 0) {
            throw new BadRequestException("INVALID_SEARCH_QUERY", "Page size must be > 0");
        }
    }
}