package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.MediaLink;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.MediaLinkRequest;
import notkirill.workshopmanager.models.dto.MediaLinkResponse;
import notkirill.workshopmanager.models.dto.mappers.MediaLinkMapper;
import notkirill.workshopmanager.models.repositories.MediaLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaLinkService {
    private final MediaLinkRepository repository;
    private final MediaLinkMapper mapper;

    @Autowired
    public MediaLinkService(MediaLinkRepository repository, MediaLinkMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<MediaLinkResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public MediaLinkResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(MediaLinkRequest request) {
        MediaLink entity = mapper.requestToEntity(request);
        MediaLink saved = repository.save(entity);
    }

    public void update(Long id, MediaLinkRequest request) {
        MediaLink entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        MediaLink saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
