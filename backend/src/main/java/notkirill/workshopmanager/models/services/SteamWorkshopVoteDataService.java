package notkirill.workshopmanager.models.services;

import notkirill.workshopmanager.models.SteamWorkshopVoteData;
import notkirill.workshopmanager.models.dto.SteamWorkshopVoteDataRequest;
import notkirill.workshopmanager.models.dto.SteamWorkshopVoteDataResponse;
import notkirill.workshopmanager.models.dto.mappers.SteamWorkshopVoteDataMapper;
import notkirill.workshopmanager.models.repositories.SteamWorkshopVoteDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SteamWorkshopVoteDataService {
    private final SteamWorkshopVoteDataRepository repository;
    private final SteamWorkshopVoteDataMapper mapper;

    @Autowired
    public SteamWorkshopVoteDataService(SteamWorkshopVoteDataRepository repository,
                                        SteamWorkshopVoteDataMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<SteamWorkshopVoteDataResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    public SteamWorkshopVoteDataResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(NoSuchElementException::new);
    }

    public void create(SteamWorkshopVoteDataRequest request) {
        SteamWorkshopVoteData entity = mapper.requestToEntity(request);
        SteamWorkshopVoteData saved = repository.save(entity);
    }

    public void update(Long id, SteamWorkshopVoteDataRequest request) {
        SteamWorkshopVoteData entity = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        mapper.updateEntityFromRequest(request, entity);
        SteamWorkshopVoteData saved = repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new NoSuchElementException();
        repository.deleteById(id);
    }
}
