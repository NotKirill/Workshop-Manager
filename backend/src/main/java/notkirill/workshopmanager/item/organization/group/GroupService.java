package notkirill.workshopmanager.item.organization.group;

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

@Service
public class GroupService {
    private final UserGroupRepository groupRepository;
    private final UserGroupItemRepository groupItemRepository;
    private final ItemInternalService internalService;
    private final PersonalItemService personalService;
    private final GameService gameService;

    @Autowired
    public GroupService(UserGroupRepository groupRepository, UserGroupItemRepository groupItemRepository,
                        ItemInternalService internalService, PersonalItemService personalService,
                        GameService gameService) {
        this.groupRepository = groupRepository;
        this.groupItemRepository = groupItemRepository;
        this.internalService = internalService;
        this.personalService = personalService;
        this.gameService = gameService;
    }

    @Transactional(readOnly = true)
    public List<CountedGroup> list(long userId, Long gameId) {
        List<UserGroup> groups = gameId == null
                ? groupRepository.findByUserIdOrderByNameAsc(userId)
                : groupRepository.findByUserIdAndGameIdOrderByNameAsc(userId, gameId);
        return groups.stream()
                .map(group -> new CountedGroup(group, groupItemRepository.countByUserGroupId(group.getId())))
                .toList();
    }

    @Transactional
    public UserGroup create(long userId, String name, long gameId) {
        gameService.requireKnownGame(gameId);
        String normalized = name.strip();
        if (groupRepository.findByUserIdAndGameIdAndNameIgnoreCase(userId, gameId, normalized).isPresent()) {
            throw new ConflictException("GROUP_EXISTS", "A group with this name already exists for the game");
        }
        return groupRepository.save(new UserGroup(userId, gameId, normalized));
    }

    @Transactional
    public CountedGroup rename(long userId, long groupId, String name) {
        UserGroup group = requireGroup(userId, groupId);
        String normalized = name.strip();
        groupRepository.findByUserIdAndGameIdAndNameIgnoreCase(userId, group.getGameId(), normalized)
                .filter(other -> other.getId() != groupId)
                .ifPresent(other -> {
                    throw new ConflictException("GROUP_EXISTS", "A group with this name already exists for the game");
                });
        group.setName(normalized);
        groupRepository.save(group);
        return new CountedGroup(group, groupItemRepository.countByUserGroupId(group.getId()));
    }

    @Transactional
    public void delete(long userId, long groupId) {
        UserGroup group = requireGroup(userId, groupId);
        groupItemRepository.deleteByUserGroupId(group.getId());
        groupRepository.delete(group);
    }

    @Transactional
    public void deleteAllOfUser(long userId) {
        for (UserGroup group : groupRepository.findByUserId(userId)) {
            groupItemRepository.deleteByUserGroupId(group.getId());
            groupRepository.delete(group);
        }
    }

    @Transactional
    public void deleteOfItems(Collection<Long> personalIds) {
        groupItemRepository.deleteByItemPersonalMetadataIdIn(personalIds);
    }

    @Transactional
    public int addItems(long userId, long groupId, Collection<Long> personalIds) {
        UserGroup group = requireGroup(userId, groupId);
        int affected = 0;
        for (ItemMetadataPersonal personal : matchingGame(userId, personalIds, group.getGameId())) {
            UserGroupItem.CompositeId id = new UserGroupItem.CompositeId(group.getId(), personal.getId());
            if (!groupItemRepository.existsById(id)) {
                groupItemRepository.save(new UserGroupItem(group.getId(), personal.getId()));
                affected++;
            }
        }
        return affected;
    }

    @Transactional
    public int removeItems(long userId, long groupId, Collection<Long> personalIds) {
        UserGroup group = requireGroup(userId, groupId);
        personalService.requireOwnedAll(userId, personalIds);
        return groupItemRepository.deleteByUserGroupIdAndItemPersonalMetadataIdIn(group.getId(), personalIds);
    }

    @Transactional
    public List<UserGroup> setItemGroups(long userId, long personalId, Collection<Long> groupIds) {
        ItemMetadataPersonal personal = personalService.requireOwned(userId, personalId);
        long gameId = internalService.require(personal.getInternalMetadataId()).getGameId();
        Set<Long> unique = new HashSet<>(groupIds);
        List<UserGroup> groups = groupRepository.findByUserIdAndIdIn(userId, unique);
        if (groups.size() != unique.size()) {
            throw new NotFoundException("GROUP_NOT_FOUND", "One or more groups were not found");
        }
        if (groups.stream().anyMatch(group -> group.getGameId() != gameId)) {
            throw new ConflictException("GROUP_GAME_MISMATCH", "Groups must belong to the item's game");
        }
        groupItemRepository.deleteByItemPersonalMetadataIdIn(List.of(personalId));
        groups.forEach(group -> groupItemRepository.save(new UserGroupItem(group.getId(), personalId)));
        return groups;
    }

    @Transactional(readOnly = true)
    public List<UserGroup> getGroupsOfItem(long personalId) {
        List<Long> groupIds = groupItemRepository.findByItemPersonalMetadataId(personalId).stream()
                .map(UserGroupItem::getUserGroupId).toList();
        return groupRepository.findAllById(groupIds);
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

    private UserGroup requireGroup(long userId, long groupId) {
        return groupRepository.findByIdAndUserId(groupId, userId)
                .orElseThrow(() -> new NotFoundException("GROUP_NOT_FOUND", "Group not found"));
    }

    public record CountedGroup(UserGroup group, long itemCount) {
    }
}
