package notkirill.workshopmanager.modspace.steam;

import notkirill.workshopmanager.common.RandomBatches;
import notkirill.workshopmanager.ratelimit.ApiClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static notkirill.workshopmanager.modspace.steam.SteamJsonUtil.getJson;
import static notkirill.workshopmanager.modspace.steam.SteamJsonUtil.text;

@Component
public class SteamUserClient {
    private final SteamProperties properties;
    private final RestClient webApi;

    @Autowired
    public SteamUserClient(SteamProperties properties,
                           @Qualifier(ApiClientConfig.STEAM_WEB_API_CLIENT) RestClient webApi) {
        this.properties = properties;
        this.webApi = webApi;
    }

    public boolean supportsPlayerSummaries() {
        return properties.hasWebApiKey();
    }

    public Map<String, SteamPlayerSummary> getPlayerSummaries(Collection<String> steamIds) {
        if (!properties.hasWebApiKey() || steamIds.isEmpty()) {
            return Map.of();
        }
        Map<String, SteamPlayerSummary> result = new HashMap<>();
        List<String> ids = new ArrayList<>(new LinkedHashSet<>(steamIds));
        for (List<String> batch : batches(ids)) {
            JsonNode root = getJson(
                    webApi, uri -> uri
                            .path("/ISteamUser/GetPlayerSummaries/v2/")
                            .queryParam("key", properties.webApiKey())
                            .queryParam("steamids", String.join(",", batch))
                            .build(), "GetPlayerSummaries"
            );
            for (JsonNode player : root.path("response").path("players")) {
                String steamId = text(player, "steamid");
                if (steamId != null) {
                    result.put(
                            steamId, new SteamPlayerSummary(
                                    steamId, text(player, "personaname"),
                                    text(player, "profileurl"), text(player, "avatarfull")
                            )
                    );
                }
            }
        }
        return result;
    }

    private List<List<String>> batches(List<String> ids) {
        return RandomBatches.split(
                ids, properties.batchMinSize(), properties.batchMaxSize(),
                ThreadLocalRandom.current()
        );
    }
}
