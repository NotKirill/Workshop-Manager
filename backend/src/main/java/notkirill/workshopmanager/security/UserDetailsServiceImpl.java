package notkirill.workshopmanager.security;

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
import java.util.function.Supplier;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserFacade userFacade;

    @Autowired
    public UserDetailsServiceImpl(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @NullMarked
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return load(() -> userFacade.getForSecurityById(Long.valueOf(userId)));
    }

    public UserDetails loadByIdentity(AuthSource source, String key) throws UsernameNotFoundException {
        return load(() -> userFacade.getForSecurityByProvider(source, key));
    }

    private UserDetails load(Supplier<UserSecurityDto> lookup) {
        try {
            return new UserPrincipal(lookup.get());
        } catch (NoSuchElementException _) {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
