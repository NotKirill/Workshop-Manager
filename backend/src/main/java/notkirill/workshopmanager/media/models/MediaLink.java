package notkirill.workshopmanager.media.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.media.enums.EntityType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Nullable
    @Column(name = "owner_user_id", nullable = true)
    private Long ownerUserId;

    @Nullable
    @Column(name = "source_key", nullable = true)
    private String sourceKey;

    @NotNull
    @Column(name = "purpose", nullable = false)
    private String purpose;

    @Column(name = "sort_order")
    private int sortOrder;

    protected MediaLink() {
        // Required by JPA
    }

    public MediaLink(long mediaItemId, EntityType entity, long entityId, @Nullable Long ownerUserId,
                     @Nullable String sourceKey, String purpose, int sortOrder) {
        this.mediaItemId = mediaItemId;
        this.entity = entity;
        this.entityId = entityId;
        this.ownerUserId = ownerUserId;
        this.sourceKey = sourceKey;
        this.purpose = purpose;
        this.sortOrder = sortOrder;
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

    @Nullable
    public Long getOwnerUserId() {
        return ownerUserId;
    }

    @Nullable
    public String getSourceKey() {
        return sourceKey;
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

    @Override
    public String toString() {
        return "MediaLink{" +
                "id=" + id +
                ", mediaItemId=" + mediaItemId +
                ", entity=" + entity +
                ", entityId=" + entityId +
                ", ownerUserId=" + ownerUserId +
                ", sourceKey='" + sourceKey + '\'' +
                ", purpose='" + purpose + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
