package notkirill.workshopmanager.modspace.steam.repositories;

import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopVoteData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SteamWorkshopVoteDataRepository extends JpaRepository<SteamWorkshopVoteData, Long> {
}