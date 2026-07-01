package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.MediaEmbed;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.MediaEmbedRequest;
import notkirill.workshopmanager.models.dto.MediaEmbedResponse;
import notkirill.workshopmanager.models.dto.mappers.MediaEmbedMapper;
import notkirill.workshopmanager.models.repositories.MediaEmbedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaEmbedService {
    private final MediaEmbedRepository repository;
    private final MediaEmbedMapper mapper;

    @Autowired
    public MediaEmbedService(MediaEmbedRepository repository, MediaEmbedMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<MediaEmbedResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public MediaEmbedResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(MediaEmbedRequest request) {
        MediaEmbed entity = mapper.requestToEntity(request);
        MediaEmbed saved = repository.save(entity);
    }

    public void update(Long id, MediaEmbedRequest request) {
        MediaEmbed entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        MediaEmbed saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
