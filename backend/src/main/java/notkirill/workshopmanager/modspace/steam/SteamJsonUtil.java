package notkirill.workshopmanager.modspace.steam;

import notkirill.workshopmanager.exception.ExternalServiceException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class SteamJsonUtil {
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    private SteamJsonUtil() {
    }

    public static ExternalServiceException failure(String operation, RestClientException e) {
        return ExternalServiceException.of("STEAM_API_ERROR", "Steam " + operation + " request failed", e);
    }

    public static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        String text = value.asString();
        return text.isEmpty() ? null : text;
    }

    public static Long parseLong(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static OffsetDateTime epoch(long seconds) {
        return seconds <= 0 ? null : OffsetDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneOffset.UTC);
    }

    public static JsonNode getJson(RestClient client, Function<UriBuilder, URI> uri, String operation) {
        String body;
        try {
            body = client.get().uri(uri).retrieve().body(String.class);
        } catch (RestClientException e) {
            throw failure(operation, e);
        }
        return parse(body, operation);
    }

    public static JsonNode parse(String body, String operation) {
        if (body == null || body.isBlank()) {
            throw new ExternalServiceException(
                    "STEAM_API_ERROR", "Steam " + operation + " returned an empty body",
                    true
            );
        }
        try {
            return JSON_MAPPER.readTree(body);
        } catch (JacksonException e) {
            throw new ExternalServiceException(
                    "STEAM_API_ERROR", "Steam " + operation + " returned invalid JSON", e,
                    true
            );
        }
    }

    public static JsonNode json(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value;
    }

    static List<String> childIds(JsonNode children) {
        List<String> ids = new ArrayList<>();
        if (children == null) {
            return ids;
        }
        for (JsonNode child : children) {
            String id = text(child, "publishedfileid");
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }
}
