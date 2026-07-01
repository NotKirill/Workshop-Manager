package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.UserItemRelation;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.UserItemRelationRequest;
import notkirill.workshopmanager.models.dto.UserItemRelationResponse;
import notkirill.workshopmanager.models.dto.mappers.UserItemRelationMapper;
import notkirill.workshopmanager.models.idClasses.UserItemRelationComplexKey;
import notkirill.workshopmanager.models.repositories.UserItemRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserItemRelationService {
    private final UserItemRelationRepository repository;
    private final UserItemRelationMapper mapper;

    @Autowired
    public UserItemRelationService(UserItemRelationRepository repository, UserItemRelationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<UserItemRelationResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public UserItemRelationResponse getById(UserItemRelationComplexKey id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(UserItemRelationRequest request) {
        UserItemRelation entity = mapper.requestToEntity(request);
        UserItemRelation saved = repository.save(entity);
    }

    public void update(UserItemRelationComplexKey id, UserItemRelationRequest request) {
        UserItemRelation entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        UserItemRelation saved = repository.save(entity);
    }

    public void delete(UserItemRelationComplexKey id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
