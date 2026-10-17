package notkirill.workshopmanager.content.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;

@Entity
@Table(name = "item_contents",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_item_contents_internal_metadata_id",
                        columnNames = {"internal_metadata_id", "edition", "attributes"})
        })
public class ItemContent {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @NotNull
    @Column(name = "internal_metadata_id", nullable = false)
    private long internalMetadataId;

    @NotNull
    @Column(name = "edition", nullable = false)
    private String edition = "latest";

    @Nullable
    @Column(name = "attributes", columnDefinition = "TEXT[]", nullable = true)
    private String[] attributes;

    @Column(name = "max_revisions")
    private short maxRevisions = 1;

    protected ItemContent() {
        // Required by JPA
    }

    public ItemContent(long internalMetadataId, String edition, String[] attributes, short maxRevisions) {
        this.internalMetadataId = internalMetadataId;
        this.edition = edition;
        this.attributes = attributes;
        this.maxRevisions = maxRevisions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getInternalMetadataId() {
        return internalMetadataId;
    }

    public void setInternalMetadataId(long internalMetadataId) {
        this.internalMetadataId = internalMetadataId;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String[] getAttributes() {
        return attributes;
    }

    public void setAttributes(String[] attributes) {
        this.attributes = attributes;
    }

    public short getMaxRevisions() {
        return maxRevisions;
    }

    public void setMaxRevisions(short maxRevisions) {
        this.maxRevisions = maxRevisions;
    }

    @Override
    public String toString() {
        return "ItemContent{" +
                "id=" + id +
                ", internalMetadataId=" + internalMetadataId +
                ", edition='" + edition + '\'' +
                ", attributes=" + Arrays.toString(attributes) +
                ", maxRevisions=" + maxRevisions +
                '}';
    }
}
