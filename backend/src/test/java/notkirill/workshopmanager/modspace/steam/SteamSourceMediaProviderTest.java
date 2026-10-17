package notkirill.workshopmanager.modspace.steam;

import notkirill.workshopmanager.media.MediaPurposes;
import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.media.enums.MediaProvider;
import notkirill.workshopmanager.media.source.SourceMedia;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopExtended;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopItem;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SteamSourceMediaProviderTest {
    private static final String PREVIEW = "https://images.steamusercontent.com/ugc/1/A/";
    private static final String SCREENSHOT = "https://images.steamusercontent.com/ugc/2/B/";

    @Test
    void mapsMainPreviewAndAdditionalPreviews() {
        SteamItemReaderService reader = mock(SteamItemReaderService.class);
        SteamWorkshopItem item = new SteamWorkshopItem("42");
        item.setPreviewUrl(PREVIEW);
        SteamWorkshopExtended extended = new SteamWorkshopExtended(
                1, null, null, (short) 0, JsonMapper.builder()
                .build().readTree("""
                                          [
                                            {"previewid": "11", "sortorder": 1, "preview_type": 0, "url": "%s"},
                                            {"previewid": "12", "sortorder": 2, "preview_type": 1, "youtubevideoid": "dQw4w9WgXcQ"},
                                            {"previewid": "13", "sortorder": 3, "preview_type": 2,
                                             "external_reference": "0123456789abcdef0123456789abcdef"},
                                            {"previewid": "14", "sortorder": 4, "preview_type": 1, "youtubevideoid": "bad id\\"><x"},
                                            {"previewid": "15", "sortorder": 5, "preview_type": 4, "url": "https://example.com/x"}
                                          ]""".formatted(SCREENSHOT)), null, null, false, false, new short[0], null
        );
        when(reader.findStoredItem("42")).thenReturn(Optional.of(new StoredSteamItem(item, extended, null, List.of())));
        SteamSourceMediaProvider provider = new SteamSourceMediaProvider(reader, mock(SteamStoreClient.class));

        List<SourceMedia> media = provider.describe(EntityType.ITEM, "42");

        assertThat(media).containsExactly(
                new SourceMedia(
                        "preview", MediaPurposes.PREVIEW, 0,
                        new SourceMedia.RemoteFile(URI.create(PREVIEW), PREVIEW)
                ),
                new SourceMedia(
                        "previews/11", MediaPurposes.SHOWCASE, 1,
                        new SourceMedia.RemoteFile(URI.create(SCREENSHOT), SCREENSHOT)
                ),
                new SourceMedia(
                        "previews/12", MediaPurposes.SHOWCASE, 2,
                        new SourceMedia.Embed(MediaProvider.YOUTUBE, "dQw4w9WgXcQ")
                ),
                new SourceMedia(
                        "previews/13", MediaPurposes.SHOWCASE, 3,
                        new SourceMedia.Embed(MediaProvider.SKETCHFAB, "0123456789abcdef0123456789abcdef")
                )
        );
    }

    @Test
    void gameCoverIsTheStoreHeaderImage() {
        SteamStoreClient store = mock(SteamStoreClient.class);
        String header = "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/440/header.jpg?t=17";
        when(store.getHeaderImage(440)).thenReturn(Optional.of(header));
        SteamSourceMediaProvider provider = new SteamSourceMediaProvider(mock(SteamItemReaderService.class), store);

        List<SourceMedia> media = provider.describe(EntityType.GAME, "440");

        assertThat(media).containsExactly(new SourceMedia(
                "cover", MediaPurposes.COVER, 0,
                new SourceMedia.RemoteFile(URI.create(header), header)
        ));
        assertThat(provider.downloadHosts()).contains(URI.create(header).getHost());
    }

    @Test
    void noCoverWithoutAHeaderImage() {
        SteamStoreClient store = mock(SteamStoreClient.class);
        when(store.getHeaderImage(7)).thenReturn(Optional.empty());
        SteamSourceMediaProvider provider = new SteamSourceMediaProvider(mock(SteamItemReaderService.class), store);

        assertThat(provider.describe(EntityType.GAME, "7")).isEmpty();
        assertThat(provider.describe(EntityType.GAME, "not-an-app")).isEmpty();
        assertThat(provider.describe(EntityType.USER, "7")).isEmpty();
    }
}
