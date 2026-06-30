package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "steam_workshop_tags",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_steam_workshop_tag_identity", columnNames = {"tag", "consumer_app_id"})
        })
public class SteamWorkshopTag {
    // TODO rename
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false)
    private long tagId;

    @NotNull
    @Column(name = "tag", nullable = false)
    private String tag;

    @Column(name = "display_name")
    private String displayName;

    @NotNull
    @Column(name = "consumer_app_id", nullable = false)
    private long consumerAppId;

    protected SteamWorkshopTag() {
        // Required by JPA
    }

    public SteamWorkshopTag(String tag, String displayName, long consumerAppId) {
        this.tag = tag;
        this.displayName = displayName;
        this.consumerAppId = consumerAppId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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
                ", tag='" + tag + '\'' +
                ", displayName='" + displayName + '\'' +
                ", consumerAppId=" + consumerAppId +
                '}';
    }
}
