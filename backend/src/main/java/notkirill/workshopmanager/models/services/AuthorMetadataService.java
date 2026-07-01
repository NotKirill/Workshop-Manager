package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.AuthorMetadata;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.AuthorMetadataRequest;
import notkirill.workshopmanager.models.dto.AuthorMetadataResponse;
import notkirill.workshopmanager.models.dto.mappers.AuthorMetadataMapper;
import notkirill.workshopmanager.models.repositories.AuthorMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorMetadataService {

    private final AuthorMetadataRepository repository;
    private final AuthorMetadataMapper mapper;

    @Autowired
    public AuthorMetadataService(AuthorMetadataRepository repository, AuthorMetadataMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<AuthorMetadataResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public AuthorMetadataResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(AuthorMetadataRequest request) {
        AuthorMetadata entity = mapper.requestToEntity(request);
        AuthorMetadata saved = repository.save(entity);
    }

    public void update(Long id, AuthorMetadataRequest request) {
        AuthorMetadata entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        AuthorMetadata saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
