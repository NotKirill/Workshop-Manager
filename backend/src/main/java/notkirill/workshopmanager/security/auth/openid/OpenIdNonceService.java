package notkirill.workshopmanager.security.auth.openid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.NoSuchElementException;

@Service
public class OpenIdNonceService {
    private final OpenIdNonceRepository repository;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public OpenIdNonceService(OpenIdNonceRepository repository, PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public boolean tryRegister(String nonce, String endpoint) {
        try {
            Boolean registered = transactionTemplate.execute(status -> {
                if (repository.existsByValueAndEndpoint(nonce, endpoint)) {
                    return false;
                }
                repository.saveAndFlush(new OpenIdNonce(nonce, endpoint));
                return true;
            });
            return Boolean.TRUE.equals(registered);
        } catch (DataIntegrityViolationException _) {
            return false;
        }
    }

    public void delete(String nonce, String endpoint) {
        if (!repository.existsByValueAndEndpoint(nonce, endpoint)) throw new NoSuchElementException();
        repository.deleteByValueAndEndpoint(nonce, endpoint);
    }

    public boolean existByNonce(String nonce, String endpoint) {
        return repository.existsByValueAndEndpoint(nonce, endpoint);
    }
}
