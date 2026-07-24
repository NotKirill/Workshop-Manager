package notkirill.workshopmanager.user;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class ClientInfoService {

    public ClientInfo getClientInfo(HttpServletRequest request) {
        return extractClientInfo(request);
    }

    private ClientInfo extractClientInfo(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }

        InetAddress ipAddress;
        try {
            ipAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }

        String userAgent = request.getHeader("User-Agent");

        return new ClientInfo(ipAddress, userAgent);
    }
}
