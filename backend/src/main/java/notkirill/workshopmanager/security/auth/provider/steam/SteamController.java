package notkirill.workshopmanager.security.auth.provider.steam;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(SteamOpenIdService.BASE_PATH)
public class SteamController {
    private final SteamWebService steamWebService;

    @Autowired
    public SteamController(SteamWebService steamWebService) {
        this.steamWebService = steamWebService;
    }

    @GetMapping
    public Map<String, String> login(@RequestParam("state") String state) {
        return Map.of("url", steamWebService.loginUrl(state));
    }

    @GetMapping(SteamOpenIdService.LOGIN_CALLBACK)
    public void callback(@RequestParam MultiValueMap<String, String> params, HttpServletResponse servletResponse)
            throws IOException {
        servletResponse.sendRedirect(steamWebService.finishLogin(params));
    }
}
