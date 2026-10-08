package notkirill.workshopmanager.item.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemProjectionService {
    private final ItemMetadataProjectionRepository repository;

    @Autowired
    public ItemProjectionService(ItemMetadataProjectionRepository repository) {
        this.repository = repository;
    }

    public Optional<ItemMetadataProjection> find(long internalId) {
        return repository.findById(internalId);
    }

    public Map<Long, ItemMetadataProjection> findAll(Collection<Long> internalIds) {
        return repository.findAllById(internalIds).stream()
                .collect(Collectors.toMap(ItemMetadataProjection::getInternalId, Function.identity()));
    }

    @Transactional
    public void upsert(long internalId, ProjectionData data) {
        ItemMetadataProjection projection = repository.findById(internalId)
                .orElseGet(() -> new ItemMetadataProjection(internalId));
        projection.setTitle(data.title());
        projection.setDescription(data.description());
        projection.setAuthorId(data.authorId());
        projection.setFileSize(data.fileSize());
        projection.setSourceTimeCreated(data.sourceTimeCreated());
        repository.save(projection);
    }

    @Transactional
    public void delete(long internalId) {
        repository.deleteById(internalId);
    }
}
