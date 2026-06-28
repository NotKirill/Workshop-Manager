package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;

@Entity
@Table(name = "item_contents",
        uniqueConstraints = {
        @UniqueConstraint(name = "uk_item_contents_internal_metadata_id", columnNames = {"internal_metadata_id"})
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

    @Column(name = "reference_count")
    private int referenceCount;

    @NotNull
    @Column(name = "edition", nullable = false)
    private String edition = "latest";

    @Column(name = "attributes", columnDefinition = "TEXT[]")
    private String[] attributes;

    @Column(name = "max_revisions")
    private short maxRevisions = 1;

    protected ItemContent() {
        // Required by JPA
    }

    public ItemContent(long internalMetadataId, int referenceCount, String edition, String[] attributes, short maxRevisions) {
        this.internalMetadataId = internalMetadataId;
        this.referenceCount = referenceCount;
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

    public int getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(int referenceCount) {
        this.referenceCount = referenceCount;
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
                ", referenceCount=" + referenceCount +
                ", edition='" + edition + '\'' +
                ", attributes=" + Arrays.toString(attributes) +
                ", maxRevisions=" + maxRevisions +
                '}';
    }
}
