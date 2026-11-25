package notkirill.workshopmanager.modspace.steam;

import jakarta.persistence.criteria.Root;
import notkirill.workshopmanager.item.library.search.ModspaceSearchFields;
import notkirill.workshopmanager.item.library.search.SearchField;
import notkirill.workshopmanager.item.library.search.SearchField.TagType;
import notkirill.workshopmanager.modspace.ModspaceType;
import notkirill.workshopmanager.modspace.steam.models.SteamVisibility;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopItem;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopItemTag;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopTag;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SteamSearchFields implements ModspaceSearchFields {
    private static final List<SearchField> FIELDS = List.of(
            SearchField.source(
                    "visibility", TagType.ENUM, SteamVisibility.class, (subquery, internalId, cb, conditions) -> {
                        Root<SteamWorkshopItem> item = subquery.from(SteamWorkshopItem.class);
                        conditions.add(cb.equal(item.get("localId"), internalId));
                        return item.get("visibility");
                    }
            ),
            SearchField.source(
                    "subscriptions", TagType.NUMERIC, null, (subquery, internalId, cb, conditions) -> {
                        Root<SteamWorkshopItem> item = subquery.from(SteamWorkshopItem.class);
                        conditions.add(cb.equal(item.get("localId"), internalId));
                        return item.get("subscriptions");
                    }
            ),
            SearchField.source(
                    "tag", TagType.STRING_LIKE, null, (subquery, internalId, cb, conditions) -> {
                        Root<SteamWorkshopItemTag> itemTag = subquery.from(SteamWorkshopItemTag.class);
                        Root<SteamWorkshopTag> tag = subquery.from(SteamWorkshopTag.class);
                        conditions.add(cb.equal(itemTag.get("localId"), internalId));
                        conditions.add(cb.equal(tag.get("tagId"), itemTag.get("tagId")));
                        return tag.get("displayName");
                    }
            )
    );

    @Override
    public ModspaceType modspace() {
        return ModspaceType.STEAM;
    }

    @Override
    public List<SearchField> fields() {
        return FIELDS;
    }
}
