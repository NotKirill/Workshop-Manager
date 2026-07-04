package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.SteamWorkshopVoteData;
import notkirill.workshopmanager.models.dto.SteamWorkshopVoteDataRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopVoteDataResponse;
import org.springframework.stereotype.Component;

@Component
public class SteamWorkshopVoteDataMapper {
    public SteamWorkshopVoteDataResponse entityToResponse(SteamWorkshopVoteData entity) {
        return new SteamWorkshopVoteDataResponse(entity.getInternalId(),
                entity.getScore(),
                entity.getVotesUp(),
                entity.getVotesDown());
    }

    public SteamWorkshopVoteData requestToEntity(SteamWorkshopVoteDataRequest request) {
        return new SteamWorkshopVoteData(request.internalId(),
                request.score(),
                request.votesUp(),
                request.votesDown());
    }

    public void updateEntityFromRequest(SteamWorkshopVoteDataRequest request, SteamWorkshopVoteData entity) {
        entity.setInternalId(request.internalId());
        entity.setScore(request.score());
        entity.setVotesUp(request.votesUp());
        entity.setVotesDown(request.votesDown());
    }
}
