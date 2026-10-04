package notkirill.workshopmanager.modspace.steam;

import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopItem;
import notkirill.workshopmanager.modspace.steam.repositories.SteamWorkshopExtendedRepository;
import notkirill.workshopmanager.modspace.steam.repositories.SteamWorkshopItemRepository;
import notkirill.workshopmanager.modspace.steam.repositories.SteamWorkshopVoteDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SteamItemReaderService {
    private final SteamWorkshopItemRepository itemRepository;
    private final SteamWorkshopExtendedRepository extendedRepository;
    private final SteamWorkshopVoteDataRepository voteRepository;
    private final SteamTagService tagService;

    @Autowired
    public SteamItemReaderService(SteamWorkshopItemRepository itemRepository,
                                  SteamWorkshopExtendedRepository extendedRepository,
                                  SteamWorkshopVoteDataRepository voteRepository,
                                  SteamTagService tagService) {
        this.itemRepository = itemRepository;
        this.extendedRepository = extendedRepository;
        this.voteRepository = voteRepository;
        this.tagService = tagService;
    }

    @Transactional(readOnly = true)
    public Optional<SteamWorkshopItem> findItem(String publishedFileId) {
        return itemRepository.findByPublishedFileId(publishedFileId);
    }
    
    @Transactional(readOnly = true)
    public List<String> getDependencyIds(String publishedFileId) {
        return itemRepository.findByPublishedFileId(publishedFileId)
                .flatMap(item -> extendedRepository.findById(item.getLocalId()))
                .map(extended -> SteamJsonUtil.childIds(extended.getChildren()))
                .orElse(List.of());
    }

    @Transactional(readOnly = true)
    public Optional<StoredSteamItem> findStoredItem(String publishedFileId) {
        return itemRepository.findByPublishedFileId(publishedFileId).map(item -> {
            long localId = item.getLocalId();
            return new StoredSteamItem(
                    item,
                    extendedRepository.findById(localId).orElse(null),
                    voteRepository.findById(localId).orElse(null),
                    tagService.getTagNames(localId)
            );
        });
    }
}
