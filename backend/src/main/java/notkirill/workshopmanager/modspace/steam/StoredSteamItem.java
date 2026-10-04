package notkirill.workshopmanager.modspace.steam;

import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopExtended;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopItem;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopVoteData;

import java.util.List;

public record StoredSteamItem(
        SteamWorkshopItem item,
        SteamWorkshopExtended extended,
        SteamWorkshopVoteData votes,
        List<String> tagNames
) {
}
