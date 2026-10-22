package notkirill.workshopmanager.item.organization.relation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.item.enums.RelationType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_item_relations")
@IdClass(UserItemRelation.CompositeId.class)
public class UserItemRelation {
    @Id
    @NotNull
    @Column(name = "source_item_personal_metadata_id", nullable = false)
    private long sourceItemPersonalMetadataId;

    @Id
    @NotNull
    @Column(name = "target_item_personal_metadata_id", nullable = false)
    private long targetItemPersonalMetadataId;

    @Id
    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "relation", columnDefinition = "relation_type", nullable = false)
    private RelationType relation;

    protected UserItemRelation() {
        // Required by JPA
    }

    public UserItemRelation(long sourceItemPersonalMetadataId, long targetItemPersonalMetadataId,
                            RelationType relation) {
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
    public String toString() {
        return "UserItemRelation{" +
                "sourceItemPersonalMetadataId=" + sourceItemPersonalMetadataId +
                ", targetItemPersonalMetadataId=" + targetItemPersonalMetadataId +
                ", relation=" + relation +
                '}';
    }

    public static class CompositeId implements Serializable {
        private long sourceItemPersonalMetadataId;
        private long targetItemPersonalMetadataId;
        private RelationType relation;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(long sourceItemPersonalMetadataId, long targetItemPersonalMetadataId,
                           RelationType relation) {
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
        public int hashCode() {
            return Objects.hash(sourceItemPersonalMetadataId, targetItemPersonalMetadataId, relation);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return sourceItemPersonalMetadataId == that.sourceItemPersonalMetadataId && targetItemPersonalMetadataId == that.targetItemPersonalMetadataId && relation == that.relation;
        }

        @Override
        public String toString() {
            return "UserItemRelation.CompositeId{" +
                    "sourceItemPersonalMetadataId=" + sourceItemPersonalMetadataId +
                    ", targetItemPersonalMetadataId=" + targetItemPersonalMetadataId +
                    ", relation=" + relation +
                    '}';
        }
    }
}
