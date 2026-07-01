package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.SteamWorkshopItem;
import notkirill.workshopmanager.models.controllers.exception.NoSuchEntityException;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopItemResponse;
import notkirill.workshopmanager.models.dto.mappers.SteamWorkshopItemMapper;
import notkirill.workshopmanager.models.repositories.SteamWorkshopItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SteamWorkshopItemService {
    private final SteamWorkshopItemRepository repository;
    private final SteamWorkshopItemMapper mapper;

    @Autowired
    public SteamWorkshopItemService(SteamWorkshopItemRepository repository, SteamWorkshopItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<SteamWorkshopItemResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public SteamWorkshopItemResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchEntityException::new);
    }

    public void create(SteamWorkshopItemRequest request) {
        SteamWorkshopItem entity = mapper.requestToEntity(request);
        SteamWorkshopItem saved = repository.save(entity);
    }

    public void update(Long id, SteamWorkshopItemRequest request) {
        SteamWorkshopItem entity = repository.findById(id)
                .orElseThrow(NoSuchEntityException::new);

        mapper.updateEntityFromRequest(request, entity);
        SteamWorkshopItem saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchEntityException();
        repository.deleteById(id);
    }
}
