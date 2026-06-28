package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.EntityType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "media_links")
public class MediaLink {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @NotNull
    @Column(name = "media_item_id", nullable = false)
    private long mediaItemId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "entity", columnDefinition = "entity_type", nullable = false)
    private EntityType entity;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    private long entityId;

    @NotNull
    @Column(name = "purpose", nullable = false)
    private String purpose;

    // TODO relocate to metadata
    @Column(name = "sort_order")
    private int sortOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private JsonNode metadata;

    protected MediaLink() {
        // Required by JPA
    }

    public MediaLink(long mediaItemId, EntityType entity, long entityId, String purpose, int sortOrder, JsonNode metadata) {
        this.mediaItemId = mediaItemId;
        this.entity = entity;
        this.entityId = entityId;
        this.purpose = purpose;
        this.sortOrder = sortOrder;
        this.metadata = metadata;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMediaItemId() {
        return mediaItemId;
    }

    public void setMediaItemId(long mediaItemId) {
        this.mediaItemId = mediaItemId;
    }

    public EntityType getEntity() {
        return entity;
    }

    public void setEntity(EntityType entity) {
        this.entity = entity;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public JsonNode getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonNode metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "MediaLink{" +
                "id=" + id +
                ", mediaItemId=" + mediaItemId +
                ", entity=" + entity +
                ", entityId=" + entityId +
                ", purpose='" + purpose + '\'' +
                ", sortOrder=" + sortOrder +
                ", metadata=" + metadata +
                '}';
    }
}
