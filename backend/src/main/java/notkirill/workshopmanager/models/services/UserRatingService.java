package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.UserRating;
import notkirill.workshopmanager.models.dto.UserRatingRequest;
import notkirill.workshopmanager.models.dto.UserRatingResponse;
import notkirill.workshopmanager.models.dto.mappers.UserRatingMapper;
import notkirill.workshopmanager.models.repositories.UserRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserRatingService {
    private final UserRatingRepository repository;
    private final UserRatingMapper mapper;

    @Autowired
    public UserRatingService(UserRatingRepository repository, UserRatingMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<UserRatingResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public UserRatingResponse getById(UserRating.CompositeId id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(UserRatingRequest request) {
        UserRating entity = mapper.requestToEntity(request);
        UserRating saved = repository.save(entity);
    }

    public void update(UserRating.CompositeId id, UserRatingRequest request) {
        UserRating entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);
        UserRating saved = repository.save(entity);
    }

    public void delete(UserRating.CompositeId id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
