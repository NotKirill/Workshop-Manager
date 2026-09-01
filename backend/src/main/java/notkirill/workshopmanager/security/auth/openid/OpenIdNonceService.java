package notkirill.workshopmanager.security.auth.openid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class OpenIdNonceService {
    private final OpenIdNonceRepository repository;

    @Autowired
    public OpenIdNonceService(OpenIdNonceRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void create(String nonce, String endpoint) {
        repository.save(new OpenIdNonce(nonce, endpoint));
    }

    public void delete(String nonce, String endpoint) {
        if (!repository.existsByValueAndEndpoint(nonce, endpoint)) throw new NoSuchElementException();
        repository.deleteByValueAndEndpoint(nonce, endpoint);
    }

    public boolean existByNonce(String nonce, String endpoint) {
        return repository.existsByValueAndEndpoint(nonce, endpoint);
    }
}
