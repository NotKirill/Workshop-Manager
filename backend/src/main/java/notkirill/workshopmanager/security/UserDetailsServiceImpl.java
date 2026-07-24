package notkirill.workshopmanager.security;

import jakarta.servlet.http.HttpServletRequest;
import notkirill.workshopmanager.SpringUtils;
import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.user.UserFacade;
import notkirill.workshopmanager.user.dto.UserSecurityDto;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserFacade userFacade;

    @Autowired
    public UserDetailsServiceImpl(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @NullMarked
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest request = SpringUtils.extractRequest();
        AuthSource authSource = getAuthProvider(request);

        UserSecurityDto userDto;
        try {
            userDto = authSource != null
                    ? userFacade.getForSecurityByProvider(authSource, username)
                    : userFacade.getForSecurityById(Long.valueOf(username));
        } catch (NoSuchElementException _) {
            throw new UsernameNotFoundException("User not found");
        }

        return new UserPrincipal(userDto);
    }

    private AuthSource getAuthProvider(HttpServletRequest request) {
        return (AuthSource) request.getAttribute("auth_source");
    }
}