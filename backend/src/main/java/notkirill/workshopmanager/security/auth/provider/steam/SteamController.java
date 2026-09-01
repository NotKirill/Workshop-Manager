package notkirill.workshopmanager.security.auth.provider.steam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import notkirill.workshopmanager.FrontendProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/steam")
public class SteamController {
    private final SteamOpenIdService steamOpenIdService;
    private final FrontendProperties frontendProperties;

    @Autowired
    public SteamController(SteamOpenIdService steamOpenIdService, FrontendProperties frontendProperties) {
        this.steamOpenIdService = steamOpenIdService;
        this.frontendProperties = frontendProperties;
    }

    @GetMapping
    public Map<String, String> login() {
        String redirectUrl = steamOpenIdService.constructRedirect("/api/auth/steam/callback");
        return Map.of("url", redirectUrl);
    }

    @GetMapping("/callback")
    public void callback(HttpServletResponse servletResponse, HttpServletRequest servletRequest) throws IOException {
        String token = steamOpenIdService.authenticate(servletRequest);
        servletResponse.sendRedirect(frontendProperties.url() + "/login-success?ott=" + token);
    }
}