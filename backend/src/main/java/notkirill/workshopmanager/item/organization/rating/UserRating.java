package notkirill.workshopmanager.item.organization.rating;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_ratings")
@IdClass(UserRating.CompositeId.class)
public class UserRating {
    @Id
    @NotNull
    @Column(name = "user_id", nullable = false)
    private long userId;

    @Id
    @NotNull
    @Column(name = "item_personal_metadata_id", nullable = false)
    private long itemPersonalMetadataId;

    @Column(name = "score")
    private short score;

    protected UserRating() {
        // Required by JPA
    }

    public UserRating(long userId, long itemPersonalMetadataId, short score) {
        this.userId = userId;
        this.itemPersonalMetadataId = itemPersonalMetadataId;
        this.score = score;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getItemPersonalMetadataId() {
        return itemPersonalMetadataId;
    }

    public void setItemPersonalMetadataId(long itemPersonalMetadataId) {
        this.itemPersonalMetadataId = itemPersonalMetadataId;
    }

    public short getScore() {
        return score;
    }

    public void setScore(short score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "UserRating{" +
                "userId=" + userId +
                ", itemPersonalMetadataId=" + itemPersonalMetadataId +
                ", score=" + score +
                '}';
    }

    public static class CompositeId implements Serializable {
        private long userId;
        private long itemPersonalMetadataId;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(long userId, long itemPersonalMetadataId) {
            this.userId = userId;
            this.itemPersonalMetadataId = itemPersonalMetadataId;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public long getItemPersonalMetadataId() {
            return itemPersonalMetadataId;
        }

        public void setItemPersonalMetadataId(long itemPersonalMetadataId) {
            this.itemPersonalMetadataId = itemPersonalMetadataId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, itemPersonalMetadataId);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return userId == that.userId && itemPersonalMetadataId == that.itemPersonalMetadataId;
        }

        @Override
        public String toString() {
            return "UserRating.CompositeId{" +
                    "userId=" + userId +
                    ", itemPersonalMetadataId=" + itemPersonalMetadataId +
                    '}';
        }
    }

}
