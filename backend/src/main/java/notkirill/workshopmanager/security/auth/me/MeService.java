package notkirill.workshopmanager.security.auth.me;

import notkirill.workshopmanager.user.User;
import notkirill.workshopmanager.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class MeService {
    private final UserService userService;

    @Autowired
    public MeService(UserService userService) {
        this.userService = userService;
    }

    public MeResponse getMe(UserDetails userDetails) {
        User user = userService.getById(Long.valueOf(userDetails.getUsername()));

        return new MeResponse(
                Long.valueOf(userDetails.getUsername()),
                user.getName(),
                user.getTimeCreated()
        );
    }
}
