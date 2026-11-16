package notkirill.workshopmanager.ratelimit;

import io.micrometer.common.KeyValue;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.client.observation.DefaultClientRequestObservationConvention;

@NullMarked
public class PathObservationConvention extends DefaultClientRequestObservationConvention {

    @Override
    protected KeyValue uri(ClientRequestObservationContext context) {
        ClientHttpRequest request = context.getCarrier();
        if (context.getUriTemplate() == null && request != null) {
            return KeyValue.of("uri", request.getURI().getPath());
        }
        return super.uri(context);
    }
}
