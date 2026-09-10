package notkirill.workshopmanager.security.auth.refresh;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.net.InetAddress;

@Embeddable
public class ClientInfo {

    @Column(name = "ip_address")
    private InetAddress ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    protected ClientInfo() {
        // Required by JPA
    }

    public ClientInfo(
            InetAddress ipAddress,
            String userAgent
    ) {
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "ipAddress=" + ipAddress +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}