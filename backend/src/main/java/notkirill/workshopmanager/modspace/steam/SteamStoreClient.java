package notkirill.workshopmanager.modspace.steam;

import notkirill.workshopmanager.ratelimit.ApiClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.Optional;

@Component
public class SteamStoreClient {
    private final RestClient storeApi;

    @Autowired
    public SteamStoreClient(@Qualifier(ApiClientConfig.STEAM_STORE_API_CLIENT) RestClient storeApi) {
        this.storeApi = storeApi;
    }

    public Optional<String> getAppName(long appId) {
        return appDetails(appId).map(data -> SteamJsonUtil.text(data, "name"));
    }

    public Optional<String> getHeaderImage(long appId) {
        return appDetails(appId).map(data -> SteamJsonUtil.text(data, "header_image"));
    }

    private Optional<JsonNode> appDetails(long appId) {
        JsonNode app = SteamJsonUtil.getJson(
                        storeApi, uri -> uri
                                .path("/api/appdetails")
                                .queryParam("appids", appId)
                                .queryParam("filters", "basic")
                                .build(), "appdetails"
                )
                .path(String.valueOf(appId));
        if (!app.path("success").asBoolean(false)) {
            return Optional.empty();
        }
        return Optional.of(app.path("data"));
    }
}
