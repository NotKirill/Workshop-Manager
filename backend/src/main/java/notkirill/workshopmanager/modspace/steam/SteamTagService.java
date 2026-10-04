package notkirill.workshopmanager.modspace.steam;

import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopItemTag;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopTag;
import notkirill.workshopmanager.modspace.steam.repositories.SteamWorkshopItemTagRepository;
import notkirill.workshopmanager.modspace.steam.repositories.SteamWorkshopTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class SteamTagService {
    private final SteamWorkshopTagRepository tagRepository;
    private final SteamWorkshopItemTagRepository itemTagRepository;

    @Autowired
    public SteamTagService(SteamWorkshopTagRepository tagRepository,
                           SteamWorkshopItemTagRepository itemTagRepository) {
        this.tagRepository = tagRepository;
        this.itemTagRepository = itemTagRepository;
    }

    @Transactional(readOnly = true)
    public List<String> getTagNames(long localId) {
        List<Long> tagIds = itemTagRepository.findByLocalId(localId).stream()
                .map(SteamWorkshopItemTag::getTagId)
                .toList();
        return tagRepository.findByTagIdIn(tagIds).stream()
                .map(tag -> tag.getDisplayName() != null ? tag.getDisplayName() : tag.getTagKey())
                .sorted()
                .toList();
    }

    @Transactional
    public void replaceTags(long localId, long consumerAppId, List<SteamPublishedFile.Tag> tags) {
        itemTagRepository.deleteByLocalId(localId);
        Set<Long> linked = new HashSet<>();
        for (SteamPublishedFile.Tag tag : tags) {
            SteamWorkshopTag tagEntity = tagRepository.findByTagKeyAndConsumerAppId(tag.key(), consumerAppId)
                    .orElseGet(() -> tagRepository.save(
                            new SteamWorkshopTag(tag.key(), tag.displayName(), consumerAppId)));

            if (tag.displayName() != null && !Objects.equals(tagEntity.getDisplayName(), tag.displayName())) {
                tagEntity.setDisplayName(tag.displayName());
                tagRepository.save(tagEntity);
            }
            if (linked.add(tagEntity.getTagId())) {
                itemTagRepository.save(new SteamWorkshopItemTag(localId, tagEntity.getTagId()));
            }
        }
    }

    @Transactional
    public void deleteLinks(long localId) {
        itemTagRepository.deleteByLocalId(localId);
    }
}
