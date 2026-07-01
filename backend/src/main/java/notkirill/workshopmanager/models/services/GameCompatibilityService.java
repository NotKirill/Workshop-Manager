package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.GameCompatibility;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.GameCompatibilityRequest;
import notkirill.workshopmanager.models.dto.GameCompatibilityResponse;
import notkirill.workshopmanager.models.dto.mappers.GameCompatibilityMapper;
import notkirill.workshopmanager.models.idClasses.GameCompatibilityComplexKey;
import notkirill.workshopmanager.models.repositories.GameCompatibilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameCompatibilityService {

    private final GameCompatibilityRepository repository;
    private final GameCompatibilityMapper mapper;

    @Autowired
    public GameCompatibilityService(GameCompatibilityRepository repository, GameCompatibilityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<GameCompatibilityResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public GameCompatibilityResponse getById(GameCompatibilityComplexKey id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(GameCompatibilityRequest request) {
        GameCompatibility entity = mapper.requestToEntity(request);
        GameCompatibility saved = repository.save(entity);
    }

    public void update(GameCompatibilityComplexKey id, GameCompatibilityRequest request) {
        GameCompatibility entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        GameCompatibility saved = repository.save(entity);
    }

    public void delete(GameCompatibilityComplexKey id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }

}
