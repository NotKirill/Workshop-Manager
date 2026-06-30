package notkirill.workshopmanager.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

import java.util.Arrays;

@Entity
@Table(name = "steam_workshop_extended")
public class SteamWorkshopExtended {
    @Id
    @NotNull
    @Column(name = "internal_id", nullable = false)
    private long internalId;

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

    protected SteamWorkshopExtended() {
        // Required by JPA
    }

    public SteamWorkshopExtended(long internalId, String previewFileSize, String shortDescription, short numChildren, JsonNode previews, JsonNode children, JsonNode kvtags, boolean maybeInappropriateSex, boolean maybeInappropriateViolence, short[] contentDescriptorIds) {
        this.internalId = internalId;
        this.previewFileSize = previewFileSize;
        this.shortDescription = shortDescription;
        this.numChildren = numChildren;
        this.previews = previews;
        this.children = children;
        this.kvtags = kvtags;
        this.maybeInappropriateSex = maybeInappropriateSex;
        this.maybeInappropriateViolence = maybeInappropriateViolence;
        this.contentDescriptorIds = contentDescriptorIds;
    }

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
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

    @Override
    public String toString() {
        return "SteamWorkshopExtended{" +
                "internalId=" + internalId +
                ", previewFileSize='" + previewFileSize + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", numChildren=" + numChildren +
                ", previews=" + previews +
                ", children=" + children +
                ", kvtags=" + kvtags +
                ", maybeInappropriateSex=" + maybeInappropriateSex +
                ", maybeInappropriateViolence=" + maybeInappropriateViolence +
                ", contentDescriptorIds=" + Arrays.toString(contentDescriptorIds) +
                '}';
    }
}
