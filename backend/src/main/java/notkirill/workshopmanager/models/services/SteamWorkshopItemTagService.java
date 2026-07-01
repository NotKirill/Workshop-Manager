package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.SteamWorkshopItemTag;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemTagRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemTagResponse;
import notkirill.workshopmanager.models.dto.mappers.SteamWorkshopItemTagMapper;
import notkirill.workshopmanager.models.idClasses.SteamWorkshopItemTagComplexKey;
import notkirill.workshopmanager.models.repositories.SteamWorkshopItemTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SteamWorkshopItemTagService {
    private final SteamWorkshopItemTagRepository repository;
    private final SteamWorkshopItemTagMapper mapper;

    @Autowired
    public SteamWorkshopItemTagService(SteamWorkshopItemTagRepository repository, SteamWorkshopItemTagMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<SteamWorkshopItemTagResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public SteamWorkshopItemTagResponse getById(SteamWorkshopItemTagComplexKey id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(SteamWorkshopItemTagRequest request) {
        SteamWorkshopItemTag entity = mapper.requestToEntity(request);
        SteamWorkshopItemTag saved = repository.save(entity);
    }

    public void update(SteamWorkshopItemTagComplexKey id, SteamWorkshopItemTagRequest request) {
        SteamWorkshopItemTag entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        SteamWorkshopItemTag saved = repository.save(entity);
    }

    public void delete(SteamWorkshopItemTagComplexKey id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
