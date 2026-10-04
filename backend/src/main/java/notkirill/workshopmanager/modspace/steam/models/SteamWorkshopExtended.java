package notkirill.workshopmanager.modspace.steam.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.Arrays;

@Entity
@Table(name = "steam_workshop_extended")
public class SteamWorkshopExtended {
    @Id
    @NotNull
    @Column(name = "local_id", nullable = false)
    private long localId;

    @Column(name = "preview_file_size")
    private String previewFileSize;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "num_children")
    private short numChildren;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "previews", columnDefinition = "jsonb")
    private JsonNode previews;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "children", columnDefinition = "jsonb")
    private JsonNode children;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "kvtags", columnDefinition = "jsonb")
    private JsonNode kvtags;

    @Column(name = "maybe_inappropriate_sex")
    private boolean maybeInappropriateSex;

    @Column(name = "maybe_inappropriate_violence")
    private boolean maybeInappropriateViolence;

    @Column(name = "content_descriptorids", columnDefinition = "SMALLINT[]")
    private short[] contentDescriptorIds;

    /**
     * Steam's update time of the item version these details were fetched at.
     */
    @Column(name = "time_updated")
    private OffsetDateTime timeUpdated;

    protected SteamWorkshopExtended() {
        // Required by JPA
    }

    public SteamWorkshopExtended(long localId, String previewFileSize, String shortDescription, short numChildren, JsonNode previews, JsonNode children, JsonNode kvtags, boolean maybeInappropriateSex, boolean maybeInappropriateViolence, short[] contentDescriptorIds, OffsetDateTime timeUpdated) {
        this.localId = localId;
        this.previewFileSize = previewFileSize;
        this.shortDescription = shortDescription;
        this.numChildren = numChildren;
        this.previews = previews;
        this.children = children;
        this.kvtags = kvtags;
        this.maybeInappropriateSex = maybeInappropriateSex;
        this.maybeInappropriateViolence = maybeInappropriateViolence;
        this.contentDescriptorIds = contentDescriptorIds;
        this.timeUpdated = timeUpdated;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public String getPreviewFileSize() {
        return previewFileSize;
    }

    public void setPreviewFileSize(String previewFileSize) {
        this.previewFileSize = previewFileSize;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public short getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(short numChildren) {
        this.numChildren = numChildren;
    }

    public JsonNode getPreviews() {
        return previews;
    }

    public void setPreviews(JsonNode previews) {
        this.previews = previews;
    }

    public JsonNode getChildren() {
        return children;
    }

    public void setChildren(JsonNode children) {
        this.children = children;
    }

    public JsonNode getKvtags() {
        return kvtags;
    }

    public void setKvtags(JsonNode kvtags) {
        this.kvtags = kvtags;
    }

    public boolean isMaybeInappropriateSex() {
        return maybeInappropriateSex;
    }

    public void setMaybeInappropriateSex(boolean maybeInappropriateSex) {
        this.maybeInappropriateSex = maybeInappropriateSex;
    }

    public boolean isMaybeInappropriateViolence() {
        return maybeInappropriateViolence;
    }

    public void setMaybeInappropriateViolence(boolean maybeInappropriateViolence) {
        this.maybeInappropriateViolence = maybeInappropriateViolence;
    }

    public short[] getContentDescriptorIds() {
        return contentDescriptorIds;
    }

    public void setContentDescriptorIds(short[] contentDescriptorIds) {
        this.contentDescriptorIds = contentDescriptorIds;
    }

    public OffsetDateTime getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(OffsetDateTime timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    @Override
    public String toString() {
        return "SteamWorkshopExtended{" +
                "localId=" + localId +
                ", previewFileSize='" + previewFileSize + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", numChildren=" + numChildren +
                ", previews=" + previews +
                ", children=" + children +
                ", kvtags=" + kvtags +
                ", maybeInappropriateSex=" + maybeInappropriateSex +
                ", maybeInappropriateViolence=" + maybeInappropriateViolence +
                ", contentDescriptorIds=" + Arrays.toString(contentDescriptorIds) +
                ", timeUpdated=" + timeUpdated +
                '}';
    }
}
