package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.SteamWorkshopVoteData;
import notkirill.workshopmanager.models.dto.SteamWorkshopVoteDataRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopVoteDataResponse;

public class SteamWorkshopVoteDataMapper {
    public SteamWorkshopVoteDataResponse fromEntity(SteamWorkshopVoteData entity) {
        return new SteamWorkshopVoteDataResponse(entity.getInternalId(),
                entity.getScore(),
                entity.getVotesUp(),
                entity.getVotesDown());


    }

    public SteamWorkshopVoteData toEntity(SteamWorkshopVoteDataRequest request) {
        return new SteamWorkshopVoteData(request.internalId(),
                request.score(),
                request.votesUp(),
                request.votesDown());
    }
}
