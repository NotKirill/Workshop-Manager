package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.GameToModspaceId;
import notkirill.workshopmanager.models.dto.GameToModspaceIdRequest;
import notkirill.workshopmanager.models.dto.GameToModspaceIdResponse;
import notkirill.workshopmanager.models.dto.mappers.GameToModspaceIdMapper;
import notkirill.workshopmanager.models.repositories.GameToModspaceIdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GameToModspaceIdService {
    private final GameToModspaceIdRepository repository;
    private final GameToModspaceIdMapper mapper;

    @Autowired
    public GameToModspaceIdService(GameToModspaceIdRepository repository, GameToModspaceIdMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<GameToModspaceIdResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public GameToModspaceIdResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(GameToModspaceIdRequest request) {
        GameToModspaceId entity = mapper.requestToEntity(request);
        GameToModspaceId saved = repository.save(entity);
    }

    public void update(Long id, GameToModspaceIdRequest request) {
        GameToModspaceId entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);
        GameToModspaceId saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
