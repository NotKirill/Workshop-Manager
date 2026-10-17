package notkirill.workshopmanager.modspace.steam;

import notkirill.workshopmanager.media.MediaPurposes;
import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.media.enums.MediaProvider;
import notkirill.workshopmanager.media.source.SourceMedia;
import notkirill.workshopmanager.media.source.SourceMediaProvider;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class SteamSourceMediaProvider implements SourceMediaProvider {
    private static final Logger log = LoggerFactory.getLogger(SteamSourceMediaProvider.class);

    // Workshop previews (user generated content) and store images (game covers)
    private static final Set<String> DOWNLOAD_HOSTS = Set.of(
            "steamuserimages-a.akamaihd.net", "images.steamusercontent.com", "shared.akamai.steamstatic.com");
    private static final int PREVIEW_IMAGE = 0;
    private static final int PREVIEW_YOUTUBE = 1;
    private static final int PREVIEW_SKETCHFAB = 2;

    private static final Pattern YOUTUBE_ID = Pattern.compile("[A-Za-z0-9_-]{11}");
    private static final Pattern SKETCHFAB_ID = Pattern.compile("[0-9a-f]{32}");

    private final SteamItemReaderService readerService;
    private final SteamStoreClient storeClient;

    @Autowired
    public SteamSourceMediaProvider(SteamItemReaderService readerService, SteamStoreClient storeClient) {
        this.readerService = readerService;
        this.storeClient = storeClient;
    }

    private static Optional<SourceMedia> file(String key, String purpose, int sortOrder, String url) {
        try {
            return Optional.of(new SourceMedia(
                    key, purpose, sortOrder,
                    new SourceMedia.RemoteFile(new URI(url), url)
            ));
        } catch (URISyntaxException _) {
            log.info("Skipped Steam media item with an invalid URL: {}", url);
            return Optional.empty();
        }
    }

    private static Optional<SourceMedia> embed(String key, int sortOrder, MediaProvider provider, String id,
                                               Pattern format) {
        if (id == null || !format.matcher(id).matches()) {
            return Optional.empty();
        }
        return Optional.of(new SourceMedia(
                key, MediaPurposes.SHOWCASE, sortOrder,
                new SourceMedia.Embed(provider, id)
        ));
    }

    @Override
    public ModspaceType modspace() {
        return ModspaceType.STEAM;
    }

    // Long TODO: implement for User, Author
    @Override
    public List<SourceMedia> describe(EntityType entity, String originalId) {
        return switch (entity) {
            case ITEM -> describeItem(originalId);
            case GAME -> describeGame(originalId);
            case USER, AUTHOR -> List.of();
        };
    }

    @Override
    public Set<String> downloadHosts() {
        return DOWNLOAD_HOSTS;
    }

    private List<SourceMedia> describeItem(String originalId) {
        Optional<StoredSteamItem> stored = readerService.findStoredItem(originalId);
        if (stored.isEmpty()) {
            return List.of();
        }
        List<SourceMedia> media = new ArrayList<>();
        String previewUrl = stored.get().item().getPreviewUrl();
        if (previewUrl != null) {
            file("preview", MediaPurposes.PREVIEW, 0, previewUrl).ifPresent(media::add);
        }
        JsonNode previews = stored.get().extended() == null ? null : stored.get().extended().getPreviews();
        if (previews == null || !previews.isArray()) {
            return media;
        }
        int position = 0;
        for (JsonNode node : previews) {
            position++;
            String id = SteamJsonUtil.text(node, "previewid");
            if (id == null) {
                continue;
            }
            String key = "previews/" + id;
            int sortOrder = node.path("sortorder").asInt(position);
            Optional<SourceMedia> entry = switch (node.path("preview_type").asInt(-1)) {
                case PREVIEW_IMAGE -> Optional.ofNullable(SteamJsonUtil.text(node, "url"))
                        .flatMap(url -> file(key, MediaPurposes.SHOWCASE, sortOrder, url));
                case PREVIEW_YOUTUBE -> embed(
                        key, sortOrder, MediaProvider.YOUTUBE,
                        SteamJsonUtil.text(node, "youtubevideoid"), YOUTUBE_ID
                );
                case PREVIEW_SKETCHFAB -> embed(
                        key, sortOrder, MediaProvider.SKETCHFAB,
                        SteamJsonUtil.text(node, "external_reference"), SKETCHFAB_ID
                );
                default -> Optional.empty();
            };
            entry.ifPresent(media::add);
        }
        return media;
    }

    private List<SourceMedia> describeGame(String appId) {
        long id;
        try {
            id = Long.parseLong(appId);
        } catch (NumberFormatException _) {
            return List.of();
        }
        return storeClient.getHeaderImage(id)
                .flatMap(url -> file("cover", MediaPurposes.COVER, 0, url))
                .map(List::of)
                .orElse(List.of());
    }
}
