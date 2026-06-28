package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.idClasses.UserRatingComplexKey;

@Entity
@Table(name = "user_ratings")
@IdClass(UserRatingComplexKey.class)
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
}
