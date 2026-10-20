package notkirill.workshopmanager.modspace.steam;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class SteamWorkshopClientTest {
    private static final String BASE_URL = "https://steam.test";

    private static final String PUBLIC_ANSWER = """
            {"response": {"result": 1, "resultcount": 2, "publishedfiledetails": [
              {"publishedfileid": "1", "result": 9},
              {"publishedfileid": "105982362", "result": 1, "creator": "76561197963829096",
               "creator_app_id": 4000, "consumer_app_id": 4000, "filename": "uploads/addons/x.gma",
               "file_size": "13889620", "file_url": "https://cdn.steamusercontent.com/ugc/1/",
               "hcontent_file": "540690546175370987", "preview_url": "https://images.steamusercontent.com/ugc/2/",
               "hcontent_preview": "540690546175406904", "title": "gm_bigcity",
               "description": "A big open city map.", "time_created": 1351783410, "time_updated": 1351783410,
               "visibility": 0, "banned": 0, "ban_reason": "", "subscriptions": 3261457, "favorited": 118296,
               "lifetime_subscriptions": 5794417, "lifetime_favorited": 136739, "views": 1531764,
               "tags": [{"tag": "Addon"}, {"tag": "map"}]}
            ]}}
            """;

    private static final String KEYED_ANSWER = """
            {"response": {"publishedfiledetails": [
              {"result": 1, "publishedfileid": "105982362", "creator": "76561197963829096",
               "creator_appid": 4000, "consumer_appid": 4000, "filename": "uploads/addons/x.gma",
               "file_size": "13889620", "preview_file_size": "55000", "file_url": "", "preview_url": "https://p/",
               "title": "gm_bigcity", "file_description": "[b]A big open city map.[/b]",
               "short_description": "A big open city map.", "time_created": 1351783410,
               "time_updated": 1351783410, "visibility": 0, "banned": false, "app_name": "Garry's Mod",
               "subscriptions": 3261457, "favorited": 118296, "lifetime_subscriptions": 5794417,
               "lifetime_favorited": 136739, "num_children": 1,
               "previews": [{"previewid": "1", "sortorder": 1, "youtubevideoid": "abc", "preview_type": 1}],
               "tags": [{"tag": "map", "display_name": "Map"}],
               "children": [{"publishedfileid": "180925247", "sortorder": 0, "file_type": 0}],
               "kvtags": [{"key": "author", "value": "x"}],
               "vote_data": {"score": 0.97, "votes_up": 100, "votes_down": 3},
               "maybe_inappropriate_sex": false, "maybe_inappropriate_violence": true,
               "content_descriptorids": [2, 5]}
            ]}}
            """;

    private static SteamWorkshopClient client(String webApiKey, HttpMethod method, String path, String answer) {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(startsWith(BASE_URL + path)))
                .andExpect(method(method))
                .andRespond(withSuccess(answer, MediaType.APPLICATION_JSON));
        return new SteamWorkshopClient(new SteamProperties(webApiKey, null, null, null), builder.build());
    }

    @Test
    void publicAnswerIsReadWithItsOwnFieldNames() {
        SteamWorkshopClient client = client(
                null, HttpMethod.POST, "/ISteamRemoteStorage/GetPublishedFileDetails/v1/",
                PUBLIC_ANSWER
        );

        List<SteamPublishedFile> files = client.getPublishedFileDetails(List.of("1", "105982362"));

        SteamPublishedFile missing = files.get(0);
        assertThat(missing.isAvailable()).isFalse();
        assertThat(missing.result()).isEqualTo(SteamPublishedFile.RESULT_FILE_NOT_FOUND);
        SteamPublishedFile file = files.get(1);
        assertThat(file.creatorAppId()).isEqualTo(4000);
        assertThat(file.consumerAppId()).isEqualTo(4000);
        assertThat(file.description()).isEqualTo("A big open city map.");
        assertThat(file.fileSize()).isEqualTo(13889620L);
        assertThat(file.tags()).containsExactly(
                new SteamPublishedFile.Tag("Addon", null),
                new SteamPublishedFile.Tag("map", null)
        );
        assertThat(file.details()).isNull();
    }

    @Test
    void keyedAnswerIsReadWithItsOwnFieldNames() {
        SteamWorkshopClient client = client(
                "key", HttpMethod.GET, "/IPublishedFileService/GetDetails/v1/",
                KEYED_ANSWER
        );

        SteamPublishedFile file = client.getPublishedFileDetails(List.of("105982362")).getFirst();

        assertThat(file.consumerAppId()).isEqualTo(4000);
        assertThat(file.description()).isEqualTo("[b]A big open city map.[/b]");
        assertThat(file.tags()).containsExactly(new SteamPublishedFile.Tag("map", "Map"));
        SteamPublishedFile.Details details = file.details();
        assertThat(details.appName()).isEqualTo("Garry's Mod");
        assertThat(details.shortDescription()).isEqualTo("A big open city map.");
        assertThat(details.previewFileSize()).isEqualTo("55000");
        assertThat(details.numChildren()).isEqualTo((short) 1);
        assertThat(details.children().toString()).contains("180925247");
        assertThat(details.previews().toString()).contains("abc");
        assertThat(details.votes()).isEqualTo(new SteamPublishedFile.Votes(0.97f, 100, 3));
        assertThat(details.maybeInappropriateViolence()).isTrue();
        assertThat(details.contentDescriptorIds()).containsExactly(2, 5);
    }

    @Test
    void fieldNamesOfTheOtherEndpointAreNotRead() {
        SteamWorkshopClient client = client(
                "key", HttpMethod.GET, "/IPublishedFileService/GetDetails/v1/", """
                        {"response": {"publishedfiledetails": [
                          {"result": 1, "publishedfileid": "7", "consumer_app_id": 4000, "description": "public name"}
                        ]}}
                        """
        );

        SteamPublishedFile file = client.getPublishedFileDetails(List.of("7")).getFirst();

        assertThat(file.consumerAppId()).isZero();
        assertThat(file.description()).isNull();
    }

    @Test
    void idLeftOutOfTheAnswerIsNotFound() {
        SteamWorkshopClient client = client(
                null, HttpMethod.POST, "/ISteamRemoteStorage/GetPublishedFileDetails/v1/",
                """
                        {"response": {"publishedfiledetails": []}}
                        """
        );

        SteamPublishedFile file = client.getPublishedFileDetails(List.of("7")).getFirst();

        assertThat(file.publishedFileId()).isEqualTo("7");
        assertThat(file.result()).isEqualTo(SteamPublishedFile.RESULT_FILE_NOT_FOUND);
    }
}
