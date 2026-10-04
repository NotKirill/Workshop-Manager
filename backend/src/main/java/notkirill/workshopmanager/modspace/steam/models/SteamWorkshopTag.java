package notkirill.workshopmanager.modspace.steam.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "steam_workshop_tags",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_steam_workshop_tag_identity", columnNames = {"tag_key", "consumer_app_id"})
        })
public class SteamWorkshopTag {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false)
    private long tagId;

    @NotNull
    @Column(name = "tag_key", nullable = false)
    private String tagKey;

    @Column(name = "display_name")
    private String displayName;

    @NotNull
    @Column(name = "consumer_app_id", nullable = false)
    private long consumerAppId;

    protected SteamWorkshopTag() {
        // Required by JPA
    }

    public SteamWorkshopTag(String tagKey, String displayName, long consumerAppId) {
        this.tagKey = tagKey;
        this.displayName = displayName;
        this.consumerAppId = consumerAppId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getConsumerAppId() {
        return consumerAppId;
    }

    public void setConsumerAppId(long consumerAppId) {
        this.consumerAppId = consumerAppId;
    }

    @Override
    public String toString() {
        return "SteamWorkshopTag{" +
                "tagId=" + tagId +
                ", tag='" + tagKey + '\'' +
                ", displayName='" + displayName + '\'' +
                ", consumerAppId=" + consumerAppId +
                '}';
    }
}
