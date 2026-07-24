package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.SteamWorkshopExtended;
import notkirill.workshopmanager.models.dto.SteamWorkshopExtendedRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopExtendedResponse;
import notkirill.workshopmanager.models.dto.mappers.SteamWorkshopExtendedMapper;
import notkirill.workshopmanager.models.repositories.SteamWorkshopExtendedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SteamWorkshopExtendedService {
    private final SteamWorkshopExtendedRepository repository;
    private final SteamWorkshopExtendedMapper mapper;

    @Autowired
    public SteamWorkshopExtendedService(SteamWorkshopExtendedRepository repository,
                                        SteamWorkshopExtendedMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<SteamWorkshopExtendedResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public SteamWorkshopExtendedResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(SteamWorkshopExtendedRequest request) {
        SteamWorkshopExtended entity = mapper.requestToEntity(request);
        SteamWorkshopExtended saved = repository.save(entity);
    }

    public void update(Long id, SteamWorkshopExtendedRequest request) {
        SteamWorkshopExtended entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);
        SteamWorkshopExtended saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
