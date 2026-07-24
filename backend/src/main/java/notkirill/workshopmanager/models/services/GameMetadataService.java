package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.GameMetadata;
import notkirill.workshopmanager.models.dto.GameMetadataRequest;
import notkirill.workshopmanager.models.dto.GameMetadataResponse;
import notkirill.workshopmanager.models.dto.mappers.GameMetadataMapper;
import notkirill.workshopmanager.models.repositories.GameMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GameMetadataService {

    private final GameMetadataRepository repository;
    private final GameMetadataMapper mapper;

    @Autowired
    public GameMetadataService(GameMetadataRepository repository, GameMetadataMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<GameMetadataResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public GameMetadataResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(GameMetadataRequest request) {
        GameMetadata entity = mapper.requestToEntity(request);
        GameMetadata saved = repository.save(entity);
    }

    public void update(Long id, GameMetadataRequest request) {
        GameMetadata entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);
        GameMetadata saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}