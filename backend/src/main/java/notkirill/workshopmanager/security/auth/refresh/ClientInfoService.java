package notkirill.workshopmanager.security.auth.refresh;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class ClientInfoService {

    public ClientInfo getClientInfo(HttpServletRequest request) {
        String ip = request.getRemoteAddr();

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
