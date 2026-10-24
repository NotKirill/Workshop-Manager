package notkirill.workshopmanager.item.library.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;

@FunctionalInterface
public interface ItemSearchCriterion {
    Predicate toPredicate(ItemSearchRoots roots, CriteriaQuery<?> query, CriteriaBuilder cb);

    default ItemSearchCriterion and(ItemSearchCriterion other) {
        return (roots, query, cb) -> cb.and(toPredicate(roots, query, cb), other.toPredicate(roots, query, cb));
    }

    default ItemSearchCriterion or(ItemSearchCriterion other) {
        return (roots, query, cb) -> cb.or(toPredicate(roots, query, cb), other.toPredicate(roots, query, cb));
    }

    default ItemSearchCriterion negate() {
        return (roots, query, cb) -> cb.not(toPredicate(roots, query, cb));
    }
}
