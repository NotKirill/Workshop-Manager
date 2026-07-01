package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.MediaItem;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.MediaItemRequest;
import notkirill.workshopmanager.models.dto.MediaItemResponse;
import notkirill.workshopmanager.models.dto.mappers.MediaItemMapper;
import notkirill.workshopmanager.models.repositories.MediaItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaItemService {
    private final MediaItemRepository repository;
    private final MediaItemMapper mapper;

    @Autowired
    public MediaItemService(MediaItemRepository repository, MediaItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<MediaItemResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public MediaItemResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(MediaItemRequest request) {
        MediaItem entity = mapper.requestToEntity(request);
        MediaItem saved = repository.save(entity);
    }

    public void update(Long id, MediaItemRequest request) {
        MediaItem entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        MediaItem saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
