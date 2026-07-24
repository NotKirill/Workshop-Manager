package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.MediaFile;
import notkirill.workshopmanager.models.dto.MediaFileRequest;
import notkirill.workshopmanager.models.dto.MediaFileResponse;
import notkirill.workshopmanager.models.dto.mappers.MediaFileMapper;
import notkirill.workshopmanager.models.repositories.MediaFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MediaFileService {
    private final MediaFileRepository repository;
    private final MediaFileMapper mapper;

    @Autowired
    public MediaFileService(MediaFileRepository repository, MediaFileMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<MediaFileResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public MediaFileResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(MediaFileRequest request) {
        MediaFile entity = mapper.requestToEntity(request);
        MediaFile saved = repository.save(entity);
    }

    public void update(Long id, MediaFileRequest request) {
        MediaFile entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);
        MediaFile saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
