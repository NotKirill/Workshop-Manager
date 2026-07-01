package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.MediaLinkOverride;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.MediaLinkOverrideRequest;
import notkirill.workshopmanager.models.dto.MediaLinkOverrideResponse;
import notkirill.workshopmanager.models.dto.mappers.MediaLinkOverrideMapper;
import notkirill.workshopmanager.models.idClasses.MediaLinkOverrideComplexKey;
import notkirill.workshopmanager.models.repositories.MediaLinkOverrideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaLinkOverrideService {
    private final MediaLinkOverrideRepository repository;
    private final MediaLinkOverrideMapper mapper;

    @Autowired
    public MediaLinkOverrideService(MediaLinkOverrideRepository repository, MediaLinkOverrideMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<MediaLinkOverrideResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public MediaLinkOverrideResponse getById(MediaLinkOverrideComplexKey id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(MediaLinkOverrideRequest request) {
        MediaLinkOverride entity = mapper.requestToEntity(request);
        MediaLinkOverride saved = repository.save(entity);
    }

    public void update(MediaLinkOverrideComplexKey id, MediaLinkOverrideRequest request) {
        MediaLinkOverride entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        MediaLinkOverride saved = repository.save(entity);
    }

    public void delete(MediaLinkOverrideComplexKey id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
