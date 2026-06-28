package notkirill.workshopmanager.models.idClasses;

import notkirill.workshopmanager.models.enums.RelationType;

import java.io.Serializable;
import java.util.Objects;

// TODO rename
public class UserItemRelationComplexKey implements Serializable {
    private long sourceItemPersonalMetadataId;
    private long targetItemPersonalMetadataId;
    private RelationType relation;

    protected UserItemRelationComplexKey() {
        // Required by JPA
    }

    public UserItemRelationComplexKey(long sourceItemPersonalMetadataId, long targetItemPersonalMetadataId, RelationType relation) {
        this.sourceItemPersonalMetadataId = sourceItemPersonalMetadataId;
        this.targetItemPersonalMetadataId = targetItemPersonalMetadataId;
        this.relation = relation;
    }

    public long getSourceItemPersonalMetadataId() {
        return sourceItemPersonalMetadataId;
    }

    public void setSourceItemPersonalMetadataId(long sourceItemPersonalMetadataId) {
        this.sourceItemPersonalMetadataId = sourceItemPersonalMetadataId;
    }

    public long getTargetItemPersonalMetadataId() {
        return targetItemPersonalMetadataId;
    }

    public void setTargetItemPersonalMetadataId(long targetItemPersonalMetadataId) {
        this.targetItemPersonalMetadataId = targetItemPersonalMetadataId;
    }

    public RelationType getRelation() {
        return relation;
    }

    public void setRelation(RelationType relation) {
        this.relation = relation;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserItemRelationComplexKey that)) return false;
        return sourceItemPersonalMetadataId == that.sourceItemPersonalMetadataId && targetItemPersonalMetadataId == that.targetItemPersonalMetadataId && relation == that.relation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceItemPersonalMetadataId, targetItemPersonalMetadataId, relation);
    }

    @Override
    public String toString() {
        return "UserItemRelationComplexKey{" +
                "sourceItemPersonalMetadataId=" + sourceItemPersonalMetadataId +
                ", targetItemPersonalMetadataId=" + targetItemPersonalMetadataId +
                ", relation=" + relation +
                '}';
    }
}
