package notkirill.workshopmanager.media.source;

import notkirill.workshopmanager.media.enums.MediaProvider;

import java.net.URI;

public record SourceMedia(String key, String purpose, int sortOrder, Content content) {
    public sealed interface Content permits RemoteFile, Embed {
        String version();
    }

    public record RemoteFile(URI url, String version) implements Content {
    }

    public record Embed(MediaProvider provider, String externalId) implements Content {
        public static String version(MediaProvider provider, String externalId) {
            return provider.name() + ":" + externalId;
        }

        @Override
        public String version() {
            return version(provider, externalId);
        }
    }
}
