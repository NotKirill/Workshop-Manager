package notkirill.workshopmanager.user;

import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.item.library.ItemCommandService;
import notkirill.workshopmanager.item.organization.group.GroupService;
import notkirill.workshopmanager.item.organization.tag.TagService;
import notkirill.workshopmanager.security.auth.refresh.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class UserDeletionService {
    private final UserService userService;
    private final ItemCommandService itemCommandService;
    private final TagService tagService;
    private final GroupService groupService;
    private final RefreshTokenService refreshTokenService;
    private final UserSecurityCache securityCache;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public UserDeletionService(UserService userService, ItemCommandService itemCommandService,
                               TagService tagService, GroupService groupService,
                               RefreshTokenService refreshTokenService, UserSecurityCache securityCache,
                               PlatformTransactionManager transactionManager) {
        this.userService = userService;
        this.itemCommandService = itemCommandService;
        this.tagService = tagService;
        this.groupService = groupService;
        this.refreshTokenService = refreshTokenService;
        this.securityCache = securityCache;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void delete(long actorUserId, long userId) {
        if (actorUserId == userId) {
            throw new BadRequestException("SELF_DELETE", "You cannot delete your own account");
        }
        userService.require(userId);
        itemCommandService.unregisterAll(userId);
        transactionTemplate.executeWithoutResult(status -> {
            tagService.deleteAllOfUser(userId);
            groupService.deleteAllOfUser(userId);
            refreshTokenService.deleteAllByUserId(userId);
            userService.delete(userId);
        });
        securityCache.evict(userId);
    }
}
