package notkirill.workshopmanager.modspace.steam.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@Table(name = "steam_workshop_items",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_steam_workshop_item_identity", columnNames = {"publishedfileid"})
        })
public class SteamWorkshopItem {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "local_id", nullable = false)
    private long localId;

    @NotNull
    @Column(name = "publishedfileid", nullable = false)
    private String publishedFileId;

    @Column(name = "creator")
    private String creator;

    @Column(name = "creator_app_id")
    private long creatorAppId;

    @Column(name = "consumer_app_id")
    private long consumerAppId;

    @Column(name = "file_size")
    private String fileSize;

    @Column(name = "preview_url")
    private String previewUrl;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "time_created")
    private OffsetDateTime timeCreated;

    @Column(name = "time_updated")
    private OffsetDateTime timeUpdated;

    @Column(name = "visibility")
    private short visibility;

    @Column(name = "subscriptions")
    private int subscriptions;

    @Column(name = "favorited")
    private int favorited;

    @Column(name = "lifetime_subscriptions")
    private int lifetimeSubscriptions;

    @Column(name = "lifetime_favorited")
    private int lifetimeFavorited;

    protected SteamWorkshopItem() {
        // Required by JPA
    }

    public SteamWorkshopItem(String publishedFileId) {
        this.publishedFileId = publishedFileId;
    }

    public long getLocalId() {
        return localId;
    }

    public String getPublishedFileId() {
        return publishedFileId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public long getCreatorAppId() {
        return creatorAppId;
    }

    public void setCreatorAppId(long creatorAppId) {
        this.creatorAppId = creatorAppId;
    }

    public long getConsumerAppId() {
        return consumerAppId;
    }

    public void setConsumerAppId(long consumerAppId) {
        this.consumerAppId = consumerAppId;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(OffsetDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    public OffsetDateTime getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(OffsetDateTime timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public short getVisibility() {
        return visibility;
    }

    public void setVisibility(short visibility) {
        this.visibility = visibility;
    }

    public int getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(int subscriptions) {
        this.subscriptions = subscriptions;
    }

    public int getFavorited() {
        return favorited;
    }

    public void setFavorited(int favorited) {
        this.favorited = favorited;
    }

    public int getLifetimeSubscriptions() {
        return lifetimeSubscriptions;
    }

    public void setLifetimeSubscriptions(int lifetimeSubscriptions) {
        this.lifetimeSubscriptions = lifetimeSubscriptions;
    }

    public int getLifetimeFavorited() {
        return lifetimeFavorited;
    }

    public void setLifetimeFavorited(int lifetimeFavorited) {
        this.lifetimeFavorited = lifetimeFavorited;
    }

    @Override
    public String toString() {
        return "SteamWorkshopItem{" +
                "localId=" + localId +
                ", publishedFileId='" + publishedFileId + '\'' +
                ", consumerAppId=" + consumerAppId +
                ", title='" + title + '\'' +
                ", timeUpdated=" + timeUpdated +
                ", visibility=" + visibility +
                '}';
    }
}
