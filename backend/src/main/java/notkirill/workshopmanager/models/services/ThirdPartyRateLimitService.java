package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.ThirdPartyRateLimit;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitRequest;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitResponse;
import notkirill.workshopmanager.models.dto.mappers.ThirdPartyRateLimitMapper;
import notkirill.workshopmanager.models.repositories.ThirdPartyRateLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ThirdPartyRateLimitService {
    private final ThirdPartyRateLimitRepository repository;
    private final ThirdPartyRateLimitMapper mapper;

    @Autowired
    public ThirdPartyRateLimitService(ThirdPartyRateLimitRepository repository, ThirdPartyRateLimitMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ThirdPartyRateLimitResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public ThirdPartyRateLimitResponse getById(String id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(ThirdPartyRateLimitRequest request) {
        ThirdPartyRateLimit entity = mapper.requestToEntity(request);
        ThirdPartyRateLimit saved = repository.save(entity);
    }

    public void update(String id, ThirdPartyRateLimitRequest request) {
        ThirdPartyRateLimit entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);
        ThirdPartyRateLimit saved = repository.save(entity);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
