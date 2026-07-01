package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.SteamWorkshopTag;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.SteamWorkshopTagRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopTagResponse;
import notkirill.workshopmanager.models.dto.mappers.SteamWorkshopTagMapper;
import notkirill.workshopmanager.models.repositories.SteamWorkshopTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SteamWorkshopTagService {
    private final SteamWorkshopTagRepository repository;
    private final SteamWorkshopTagMapper mapper;

    @Autowired
    public SteamWorkshopTagService(SteamWorkshopTagRepository repository, SteamWorkshopTagMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<SteamWorkshopTagResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public SteamWorkshopTagResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(SteamWorkshopTagRequest request) {
        SteamWorkshopTag entity = mapper.requestToEntity(request);
        SteamWorkshopTag saved = repository.save(entity);
    }

    public void update(Long id, SteamWorkshopTagRequest request) {
        SteamWorkshopTag entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        SteamWorkshopTag saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
