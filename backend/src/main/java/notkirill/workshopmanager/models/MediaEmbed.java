package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.MediaProvider;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "media_embeds")
public class MediaEmbed {
    @Id
    @NotNull
    @Column(name = "media_item_id", nullable = false)
    private long mediaItemId;

    @NotNull
    @Column(name = "external_id", nullable = false)
    private String externalId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "provider", columnDefinition = "media_provider", nullable = false)
    private MediaProvider provider;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private JsonNode metadata;

    protected MediaEmbed() {
        // Required by JPA
    }

    public MediaEmbed(long mediaItemId, String externalId, MediaProvider provider, JsonNode metadata) {
        this.mediaItemId = mediaItemId;
        this.externalId = externalId;
        this.provider = provider;
        this.metadata = metadata;
    }

    public long getMediaItemId() {
        return mediaItemId;
    }

    public void setMediaItemId(long mediaItemId) {
        this.mediaItemId = mediaItemId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public MediaProvider getProvider() {
        return provider;
    }

    public void setProvider(MediaProvider provider) {
        this.provider = provider;
    }

    public JsonNode getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonNode metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "MediaEmbed{" +
                "mediaItemId=" + mediaItemId +
                ", externalId='" + externalId + '\'' +
                ", provider=" + provider +
                ", metadata=" + metadata +
                '}';
    }
}
