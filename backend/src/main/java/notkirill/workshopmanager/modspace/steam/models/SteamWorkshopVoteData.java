package notkirill.workshopmanager.modspace.steam.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "steam_workshop_vote_data")
public class SteamWorkshopVoteData {
    @Id
    @NotNull
    @Column(name = "internal_id", nullable = false)
    private long internalId;

    @Column(name = "score")
    private float score;

    @Column(name = "votes_up")
    private int votesUp;

    @Column(name = "votes_down")
    private int votesDown;

    protected SteamWorkshopVoteData() {
        // Required by JPA
    }

    public SteamWorkshopVoteData(long internalId, float score, int votesUp, int votesDown) {
        this.internalId = internalId;
        this.score = score;
        this.votesUp = votesUp;
        this.votesDown = votesDown;
    }

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getVotesUp() {
        return votesUp;
    }

    public void setVotesUp(int votesUp) {
        this.votesUp = votesUp;
    }

    public int getVotesDown() {
        return votesDown;
    }

    public void setVotesDown(int votesDown) {
        this.votesDown = votesDown;
    }

    @Override
    public String toString() {
        return "SteamWorkshopVoteData{" +
                "internalId=" + internalId +
                ", score=" + score +
                ", votesUp=" + votesUp +
                ", votesDown=" + votesDown +
                '}';
    }
}
