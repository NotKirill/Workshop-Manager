package notkirill.workshopmanager.modspace.steam.web;

import notkirill.workshopmanager.modspace.steam.SteamJsonUtil;
import notkirill.workshopmanager.modspace.steam.StoredSteamItem;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopExtended;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopItem;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopVoteData;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Component
public class SteamDetailsMapper {
    public SteamDetailsResponse toResponse(StoredSteamItem stored) {
        SteamWorkshopItem item = stored.item();
        SteamWorkshopExtended extended = stored.extended();
        SteamWorkshopVoteData votes = stored.votes();
        return new SteamDetailsResponse(
                item.getPublishedFileId(),
                item.getConsumerAppId(),
                item.getCreatorAppId(),
                item.getVisibility(),
                item.getSubscriptions(),
                item.getFavorited(),
                item.getLifetimeSubscriptions(),
                item.getLifetimeFavorited(),
                votes != null ? votes.getVotesUp() : null,
                votes != null ? votes.getVotesDown() : null,
                votes != null ? votes.getScore() : null,
                stored.tagNames(),
                extended != null ? parsePreviews(extended.getPreviews()) : List.of(),
                extended != null ? parseLinks(extended.getKvtags()) : List.of(),
                extended != null ? extended.getContentDescriptorIds() : new short[0],
                extended != null && extended.isMaybeInappropriateSex(),
                extended != null && extended.isMaybeInappropriateViolence(),
                extended != null ? extended.getShortDescription() : null
        );
    }

    private List<SteamDetailsResponse.Preview> parsePreviews(JsonNode json) {
        List<SteamDetailsResponse.Preview> previews = new ArrayList<>();
        for (JsonNode node : readArray(json)) {
            int type = node.path("preview_type").asInt(0);
            switch (type) {
                case 0 -> previews.add(new SteamDetailsResponse.Preview(
                        "IMAGE",
                        SteamJsonUtil.text(node, "url"), null
                ));
                case 1 -> {
                    String videoId = SteamJsonUtil.text(node, "youtubevideoid");
                    if (videoId != null) {
                        previews.add(new SteamDetailsResponse.Preview(
                                "YOUTUBE",
                                "https://www.youtube-nocookie.com/embed/" + videoId, videoId
                        ));
                    }
                }
                case 2 -> {
                    String reference = SteamJsonUtil.text(node, "external_reference");
                    if (reference != null) {
                        previews.add(new SteamDetailsResponse.Preview(
                                "SKETCHFAB",
                                "https://sketchfab.com/models/" + reference + "/embed", reference
                        ));
                    }
                }
                default -> {
                    String url = SteamJsonUtil.text(node, "url");
                    if (url != null) {
                        previews.add(new SteamDetailsResponse.Preview("OTHER", url, null));
                    }
                }
            }
        }
        return previews;
    }

    private List<SteamDetailsResponse.Link> parseLinks(JsonNode json) {
        List<SteamDetailsResponse.Link> links = new ArrayList<>();
        for (JsonNode node : readArray(json)) {
            String key = SteamJsonUtil.text(node, "key");
            String value = SteamJsonUtil.text(node, "value");
            if (key != null && value != null) {
                links.add(new SteamDetailsResponse.Link(key, value));
            }
        }
        return links;
    }

    private Iterable<JsonNode> readArray(JsonNode json) {
        return json != null && json.isArray() ? json : List.of();
    }
}
