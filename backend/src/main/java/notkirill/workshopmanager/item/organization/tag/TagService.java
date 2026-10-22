package notkirill.workshopmanager.item.organization.tag;

import notkirill.workshopmanager.exception.ConflictException;
import notkirill.workshopmanager.exception.NotFoundException;
import notkirill.workshopmanager.game.GameService;
import notkirill.workshopmanager.item.personal.ItemMetadataPersonal;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.item.shared.ItemInternalService;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService {
    private final UserTagRepository tagRepository;
    private final UserItemTagRepository itemTagRepository;
    private final ItemInternalService internalService;
    private final PersonalItemService personalService;
    private final GameService gameService;

    @Autowired
    public TagService(UserTagRepository tagRepository, UserItemTagRepository itemTagRepository,
                      ItemInternalService internalService, PersonalItemService personalService,
                      GameService gameService) {
        this.tagRepository = tagRepository;
        this.itemTagRepository = itemTagRepository;
        this.internalService = internalService;
        this.personalService = personalService;
        this.gameService = gameService;
    }

    @Transactional(readOnly = true)
    public List<CountedTag> list(long userId, Long gameId) {
        List<UserTag> tags = gameId == null
                ? tagRepository.findByUserIdOrderByDisplayNameAsc(userId)
                : tagRepository.findByUserIdAndGameIdOrderByDisplayNameAsc(userId, gameId);
        return tags.stream()
                .map(tag -> new CountedTag(tag, itemTagRepository.countByTagId(tag.getId())))
                .toList();
    }

    @Transactional
    public UserTag create(long userId, String name, long gameId) {
        gameService.requireKnownGame(gameId);
        String normalized = name.strip();
        if (tagRepository.findByUserIdAndGameIdAndDisplayNameIgnoreCase(userId, gameId, normalized).isPresent()) {
            throw new ConflictException("TAG_EXISTS", "A tag with this name already exists for the game");
        }
        return tagRepository.save(new UserTag(userId, normalized, gameId));
    }

    @Transactional
    public CountedTag rename(long userId, long tagId, String name) {
        UserTag tag = requireTag(userId, tagId);
        String normalized = name.strip();
        tagRepository.findByUserIdAndGameIdAndDisplayNameIgnoreCase(userId, tag.getGameId(), normalized)
                .filter(other -> other.getId() != tagId)
                .ifPresent(other -> {
                    throw new ConflictException("TAG_EXISTS", "A tag with this name already exists for the game");
                });
        tag.setDisplayName(normalized);
        tagRepository.save(tag);
        return new CountedTag(tag, itemTagRepository.countByTagId(tag.getId()));
    }

    @Transactional
    public void delete(long userId, long tagId) {
        UserTag tag = requireTag(userId, tagId);
        itemTagRepository.deleteByTagId(tag.getId());
        tagRepository.delete(tag);
    }

    @Transactional
    public void deleteAllOfUser(long userId) {
        for (UserTag tag : tagRepository.findByUserId(userId)) {
            itemTagRepository.deleteByTagId(tag.getId());
            tagRepository.delete(tag);
        }
    }

    @Transactional
    public void deleteOfItems(Collection<Long> personalIds) {
        itemTagRepository.deleteByItemPersonalMetadataIdIn(personalIds);
    }

    @Transactional
    public int addToItems(long userId, long tagId, Collection<Long> personalIds) {
        UserTag tag = requireTag(userId, tagId);
        int affected = 0;
        for (ItemMetadataPersonal personal : matchingGame(userId, personalIds, tag.getGameId())) {
            UserItemTag.CompositeId id = new UserItemTag.CompositeId(tag.getId(), personal.getId());
            if (!itemTagRepository.existsById(id)) {
                itemTagRepository.save(new UserItemTag(tag.getId(), personal.getId()));
                affected++;
            }
        }
        return affected;
    }

    @Transactional
    public int removeFromItems(long userId, long tagId, Collection<Long> personalIds) {
        UserTag tag = requireTag(userId, tagId);
        personalService.requireOwnedAll(userId, personalIds);
        return itemTagRepository.deleteByTagIdAndItemPersonalMetadataIdIn(tag.getId(), personalIds);
    }

    @Transactional
    public List<UserTag> setItemTags(long userId, long personalId, Collection<Long> tagIds) {
        ItemMetadataPersonal personal = personalService.requireOwned(userId, personalId);
        long gameId = gameOf(personal);
        Set<Long> unique = new HashSet<>(tagIds);
        List<UserTag> tags = tagRepository.findByUserIdAndIdIn(userId, unique);
        if (tags.size() != unique.size()) {
            throw new NotFoundException("TAG_NOT_FOUND", "One or more tags were not found");
        }
        if (tags.stream().anyMatch(tag -> tag.getGameId() != gameId)) {
            throw new ConflictException("TAG_GAME_MISMATCH", "Tags must belong to the item's game");
        }
        itemTagRepository.deleteByItemPersonalMetadataIdIn(List.of(personalId));
        tags.forEach(tag -> itemTagRepository.save(new UserItemTag(tag.getId(), personalId)));
        return tags;
    }

    @Transactional(readOnly = true)
    public Map<Long, List<UserTag>> getTagsOfItems(Collection<Long> personalIds) {
        if (personalIds.isEmpty()) {
            return Map.of();
        }
        List<UserItemTag> links = itemTagRepository.findByItemPersonalMetadataIdIn(personalIds);
        Map<Long, UserTag> tags = tagRepository.findAllById(
                        links.stream().map(UserItemTag::getTagId).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(UserTag::getId, tag -> tag));
        Map<Long, List<UserTag>> result = new HashMap<>();
        for (UserItemTag link : links) {
            UserTag tag = tags.get(link.getTagId());
            if (tag != null) {
                result.computeIfAbsent(link.getItemPersonalMetadataId(), id -> new ArrayList<>())
                        .add(tag);
            }
        }
        return result;
    }

    private List<ItemMetadataPersonal> matchingGame(long userId, Collection<Long> personalIds, long gameId) {
        List<ItemMetadataPersonal> personals = personalService.requireOwnedAll(userId, personalIds);
        Map<Long, ItemMetadataInternal> internals = internalService.findAll(
                personals.stream().map(ItemMetadataPersonal::getInternalMetadataId).toList());
        return personals.stream()
                .filter(p -> internals.containsKey(p.getInternalMetadataId())
                        && internals.get(p.getInternalMetadataId()).getGameId() == gameId)
                .toList();
    }

    private long gameOf(ItemMetadataPersonal personal) {
        return internalService.require(personal.getInternalMetadataId()).getGameId();
    }

    private UserTag requireTag(long userId, long tagId) {
        return tagRepository.findByIdAndUserId(tagId, userId)
                .orElseThrow(() -> new NotFoundException("TAG_NOT_FOUND", "Tag not found"));
    }

    public record CountedTag(UserTag tag, long itemCount) {
    }
}
