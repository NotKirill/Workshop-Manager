package notkirill.workshopmanager.security.auth.me;

import notkirill.workshopmanager.user.UserFacade;
import notkirill.workshopmanager.user.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class MeService {
    private final UserFacade userFacade;

    @Autowired
    public MeService(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public MeResponse getMe(UserDetails userDetails) {
        UserResponseDto userDto = userFacade.getForClientById(Long.valueOf(userDetails.getUsername()));

        return new MeResponse(
                Long.valueOf(userDetails.getUsername()),
                userDto.name(),
                userDto.timeCreated()
        );
    }
}
