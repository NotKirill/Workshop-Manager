package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.ThirdPartyRateLimits;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitsRequest;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitsResponse;
import notkirill.workshopmanager.models.dto.mappers.ThirdPartyRateLimitsMapper;
import notkirill.workshopmanager.models.repositories.ThirdPartyRateLimitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThirdPartyRateLimitsService {
    private final ThirdPartyRateLimitsRepository repository;
    private final ThirdPartyRateLimitsMapper mapper;

    @Autowired
    public ThirdPartyRateLimitsService(ThirdPartyRateLimitsRepository repository, ThirdPartyRateLimitsMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ThirdPartyRateLimitsResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public ThirdPartyRateLimitsResponse getById(String id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(ThirdPartyRateLimitsRequest request) {
        ThirdPartyRateLimits entity = mapper.requestToEntity(request);
        ThirdPartyRateLimits saved = repository.save(entity);
    }

    public void update(String id, ThirdPartyRateLimitsRequest request) {
        ThirdPartyRateLimits entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        ThirdPartyRateLimits saved = repository.save(entity);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
