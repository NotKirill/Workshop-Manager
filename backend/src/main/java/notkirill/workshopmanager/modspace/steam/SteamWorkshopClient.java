package notkirill.workshopmanager.modspace.steam;

import notkirill.workshopmanager.common.RandomBatches;
import notkirill.workshopmanager.ratelimit.ApiClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import static notkirill.workshopmanager.modspace.steam.SteamJsonUtil.*;

@Component
public class SteamWorkshopClient {
    private final SteamProperties properties;
    private final RestClient webApi;

    @Autowired
    public SteamWorkshopClient(SteamProperties properties,
                               @Qualifier(ApiClientConfig.STEAM_WEB_API_CLIENT) RestClient webApi) {
        this.properties = properties;
        this.webApi = webApi;
    }

    public List<SteamPublishedFile> getPublishedFileDetails(List<String> publishedFileIds) {
        List<SteamPublishedFile> result = new ArrayList<>();
        for (List<String> batch : batches(publishedFileIds)) {
            result.addAll(properties.hasWebApiKey() ? getDetailsWithKey(batch) : getDetailsPublic(batch));
        }
        return result;
    }

    private List<List<String>> batches(List<String> ids) {
        return RandomBatches.split(
                ids, properties.batchMinSize(), properties.batchMaxSize(),
                ThreadLocalRandom.current()
        );
    }

    private List<SteamPublishedFile> getDetailsWithKey(List<String> ids) {
        JsonNode root = getJson(
                webApi, uri -> {
                    uri.path("/IPublishedFileService/GetDetails/v1/")
                            .queryParam("key", properties.webApiKey())
                            .queryParam("includetags", true)
                            .queryParam("includeadditionalpreviews", true)
                            .queryParam("includechildren", true)
                            .queryParam("includekvtags", true)
                            .queryParam("includevotes", true)
                            .queryParam("includemetadata", true)
                            .queryParam("strip_description_bbcode", false);
                    for (int i = 0; i < ids.size(); i++) {
                        uri.queryParam("publishedfileids[" + i + "]", ids.get(i));
                    }
                    return uri.build();
                }, "GetDetails"
        );
        return parseDetails(root.path("response").path("publishedfiledetails"), ids, this::fromGetDetails);
    }

    private List<SteamPublishedFile> getDetailsPublic(List<String> ids) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("itemcount", String.valueOf(ids.size()));
        for (int i = 0; i < ids.size(); i++) {
            form.add("publishedfileids[" + i + "]", ids.get(i));
        }
        String body;
        try {
            body = webApi.post()
                    .uri("/ISteamRemoteStorage/GetPublishedFileDetails/v1/")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException e) {
            throw failure("GetPublishedFileDetails", e);
        }
        JsonNode root = parse(body, "GetPublishedFileDetails");
        return parseDetails(
                root.path("response").path("publishedfiledetails"), ids,
                this::fromPublishedFileDetails
        );
    }

    private List<SteamPublishedFile> parseDetails(JsonNode details, List<String> requestedIds,
                                                  Function<JsonNode, SteamPublishedFile> mapper) {
        Map<String, SteamPublishedFile> byId = new HashMap<>();
        for (JsonNode node : details) {
            String id = text(node, "publishedfileid");
            if (id == null) {
                continue;
            }
            int result = node.path("result").asInt(SteamPublishedFile.RESULT_FILE_NOT_FOUND);
            byId.put(
                    id, result == SteamPublishedFile.RESULT_OK
                            ? mapper.apply(node) : SteamPublishedFile.unavailable(id, result)
            );
        }
        List<SteamPublishedFile> result = new ArrayList<>();
        for (String id : requestedIds) {
            result.add(byId.getOrDefault(
                    id,
                    SteamPublishedFile.unavailable(id, SteamPublishedFile.RESULT_FILE_NOT_FOUND)
            ));
        }
        return result;
    }

    private SteamPublishedFile fromPublishedFileDetails(JsonNode n) {
        return new SteamPublishedFile(
                text(n, "publishedfileid"),
                SteamPublishedFile.RESULT_OK,
                text(n, "creator"),
                n.path("creator_app_id").asLong(0),
                n.path("consumer_app_id").asLong(0),
                parseLong(text(n, "file_size")),
                text(n, "preview_url"),
                text(n, "title"),
                text(n, "description"),
                epoch(n.path("time_created").asLong(0)),
                epoch(n.path("time_updated").asLong(0)),
                (short) n.path("visibility").asInt(0),
                n.path("subscriptions").asInt(0),
                n.path("favorited").asInt(0),
                n.path("lifetime_subscriptions").asInt(0),
                n.path("lifetime_favorited").asInt(0),
                tags(n, false),
                null
        );
    }

    private SteamPublishedFile fromGetDetails(JsonNode n) {
        JsonNode votes = n.path("vote_data");
        return new SteamPublishedFile(
                text(n, "publishedfileid"),
                SteamPublishedFile.RESULT_OK,
                text(n, "creator"),
                n.path("creator_appid").asLong(0),
                n.path("consumer_appid").asLong(0),
                parseLong(text(n, "file_size")),
                text(n, "preview_url"),
                text(n, "title"),
                text(n, "file_description"),
                epoch(n.path("time_created").asLong(0)),
                epoch(n.path("time_updated").asLong(0)),
                (short) n.path("visibility").asInt(0),
                n.path("subscriptions").asInt(0),
                n.path("favorited").asInt(0),
                n.path("lifetime_subscriptions").asInt(0),
                n.path("lifetime_favorited").asInt(0),
                tags(n, true),
                new SteamPublishedFile.Details(
                        text(n, "app_name"),
                        text(n, "short_description"),
                        text(n, "preview_file_size"),
                        (short) n.path("num_children").asInt(0),
                        json(n, "previews"),
                        json(n, "children"),
                        json(n, "kvtags"),
                        votes.isObject()
                                ? new SteamPublishedFile.Votes(
                                (float) votes.path("score").asDouble(0),
                                votes.path("votes_up").asInt(0), votes.path("votes_down").asInt(0)
                        )
                                : null,
                        n.path("maybe_inappropriate_sex").asBoolean(false),
                        n.path("maybe_inappropriate_violence").asBoolean(false),
                        contentDescriptorIds(n)
                )
        );
    }

    private List<SteamPublishedFile.Tag> tags(JsonNode n, boolean withDisplayNames) {
        List<SteamPublishedFile.Tag> tags = new ArrayList<>();
        for (JsonNode tag : n.path("tags")) {
            String key = text(tag, "tag");
            if (key != null) {
                tags.add(new SteamPublishedFile.Tag(key, withDisplayNames ? text(tag, "display_name") : null));
            }
        }
        return tags;
    }

    private short[] contentDescriptorIds(JsonNode n) {
        JsonNode ids = n.path("content_descriptorids");
        short[] result = new short[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            result[i] = (short) ids.get(i).asInt();
        }
        return result;
    }
}
