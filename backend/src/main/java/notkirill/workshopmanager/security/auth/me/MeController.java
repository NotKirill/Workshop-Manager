package notkirill.workshopmanager.security.auth.me;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MeController {
    private final MeService meService;

    @Autowired
    public MeController(MeService meService) {
        this.meService = meService;
    }

    @GetMapping("/me")
    public MeResponse getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return meService.getMe(userDetails);
    }
}