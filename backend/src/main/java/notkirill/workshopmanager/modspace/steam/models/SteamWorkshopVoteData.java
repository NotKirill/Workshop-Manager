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
    @Column(name = "local_id", nullable = false)
    private long localId;

    @Column(name = "score")
    private float score;

    @Column(name = "votes_up")
    private int votesUp;

    @Column(name = "votes_down")
    private int votesDown;

    protected SteamWorkshopVoteData() {
        // Required by JPA
    }

    public SteamWorkshopVoteData(long localId, float score, int votesUp, int votesDown) {
        this.localId = localId;
        this.score = score;
        this.votesUp = votesUp;
        this.votesDown = votesDown;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
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
                "localId=" + localId +
                ", score=" + score +
                ", votesUp=" + votesUp +
                ", votesDown=" + votesDown +
                '}';
    }
}
