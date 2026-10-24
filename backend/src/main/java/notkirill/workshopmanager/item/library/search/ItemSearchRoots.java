package notkirill.workshopmanager.item.library.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import notkirill.workshopmanager.item.organization.rating.UserRating;
import notkirill.workshopmanager.item.personal.ItemMetadataPersonal;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.item.shared.ItemMetadataProjection;

public record ItemSearchRoots(
        Root<ItemMetadataPersonal> personal,
        Join<ItemMetadataPersonal, ItemMetadataInternal> internal,
        Join<ItemMetadataPersonal, ItemMetadataProjection> projection,
        Join<ItemMetadataPersonal, UserRating> rating
) {
    public static ItemSearchRoots join(Root<ItemMetadataPersonal> personal, CriteriaBuilder cb) {
        Join<ItemMetadataPersonal, ItemMetadataInternal> internal = personal.join(ItemMetadataInternal.class);
        internal.on(cb.equal(internal.get("id"), personal.get("internalMetadataId")));
        Join<ItemMetadataPersonal, ItemMetadataProjection> projection =
                personal.join(ItemMetadataProjection.class, JoinType.LEFT);
        projection.on(cb.equal(projection.get("internalId"), personal.get("internalMetadataId")));
        Join<ItemMetadataPersonal, UserRating> rating = personal.join(UserRating.class, JoinType.LEFT);
        rating.on(cb.equal(rating.get("itemPersonalMetadataId"), personal.get("id")));
        return new ItemSearchRoots(personal, internal, projection, rating);
    }
}
