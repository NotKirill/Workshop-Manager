package notkirill.workshopmanager;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public final class SpringUtils {
    private SpringUtils() {

    }

    public static Optional<HttpServletRequest> tryExtractRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return Optional.empty();
        }

        return Optional.of(attributes.getRequest());
    }

    public static HttpServletRequest extractRequest() {
        return tryExtractRequest().orElseThrow(
                () -> new IllegalStateException("No request context available"));
    }
}
