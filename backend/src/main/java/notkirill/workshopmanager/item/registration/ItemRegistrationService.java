package notkirill.workshopmanager.item.registration;

import jakarta.annotation.Nullable;
import notkirill.workshopmanager.exception.ApiException;
import notkirill.workshopmanager.game.GameService;
import notkirill.workshopmanager.item.enums.ItemLifecycleState;
import notkirill.workshopmanager.item.enums.ItemSourceStatus;
import notkirill.workshopmanager.item.personal.ItemMetadataPersonal;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.item.registration.RegistrationResult.Status;
import notkirill.workshopmanager.item.shared.ItemInternalService;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.item.shared.ItemMetadataProjection;
import notkirill.workshopmanager.item.shared.ItemProjectionService;
import notkirill.workshopmanager.item.sync.SourceFetchService;
import notkirill.workshopmanager.item.sync.SyncOutcome;
import notkirill.workshopmanager.item.web.dto.ExternalItemRegisteringRequest;
import notkirill.workshopmanager.item.web.dto.LocalItemRegisteringRequest;
import notkirill.workshopmanager.modspace.ModspaceAdapter;
import notkirill.workshopmanager.modspace.ModspaceRegistry;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

@Service
public class ItemRegistrationService {
    public static final int MAX_ITEMS_PER_REQUEST = 500;
    private static final Logger log = LoggerFactory.getLogger(ItemRegistrationService.class);

    private final ModspaceRegistry modspaceRegistry;
    private final SourceFetchService sourceFetchService;
    private final ItemInternalService internalService;
    private final ItemProjectionService projectionService;
    private final PersonalItemService personalService;
    private final GameService gameService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public ItemRegistrationService(ModspaceRegistry modspaceRegistry, SourceFetchService sourceFetchService,
                                   ItemInternalService internalService, ItemProjectionService projectionService,
                                   PersonalItemService personalService, GameService gameService,
                                   PlatformTransactionManager transactionManager) {
        this.modspaceRegistry = modspaceRegistry;
        this.sourceFetchService = sourceFetchService;
        this.internalService = internalService;
        this.projectionService = projectionService;
        this.personalService = personalService;
        this.gameService = gameService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    private static RegistrationResult result(ModspaceType modspace, String originalId, Status status,
                                             Long personalId, Long internalId, String title, String reason,
                                             Long dependencyOf) {
        return new RegistrationResult(
                modspace, originalId, status, personalId, internalId, title, reason,
                dependencyOf
        );
    }

    @Nullable
    private static String unavailableReason(boolean unavailable, @Nullable String title) {
        if (!unavailable) {
            return null;
        }
        return title != null ? "SOURCE_UNAVAILABLE" : "SOURCE_UNKNOWN";
    }

    public List<RegistrationResult> registerExternal(long userId, ExternalItemRegisteringRequest request) {
        List<RegistrationResult> results = new ArrayList<>();
        Map<ModspaceType, Set<String>> byModspace = new LinkedHashMap<>();
        for (ExternalItemRegisteringRequest.ModspaceItems group : request.items()) {
            ModspaceAdapter adapter = modspaceRegistry.get(group.modspace());
            for (String rawId : group.originalIds()) {
                String originalId = rawId.strip();
                if (!adapter.isValidOriginalId(originalId)) {
                    results.add(result(
                            group.modspace(), originalId, Status.INVALID, null, null, null,
                            "INVALID_ID", null
                    ));
                    continue;
                }
                byModspace.computeIfAbsent(group.modspace(), m -> new LinkedHashSet<>()).add(originalId);
            }
        }

        for (Map.Entry<ModspaceType, Set<String>> entry : byModspace.entrySet()) {
            results.addAll(registerResolved(userId, entry.getKey(), entry.getValue(), request.autoBackup(), null));
        }
        return results;
    }

    public RegistrationResult registerLocal(long userId, LocalItemRegisteringRequest request) {
        long gameId;
        if (request.gameId() != null) {
            gameService.requireKnownGame(request.gameId());
            gameId = request.gameId();
        } else {
            gameId = gameService.create(request.gameName()).getId();
        }

        String originalId = UUID.randomUUID().toString();
        ItemMetadataPersonal personal = transactionTemplate.execute(status -> {
            ItemMetadataInternal internal = internalService.createLocal(gameId, originalId);
            return personalService.save(new ItemMetadataPersonal(
                    userId, internal.getId(),
                    request.title(), request.description(), request.userCommentary(), false
            ));
        });
        return result(
                ModspaceType.LOCAL, originalId, Status.REGISTERED, personal.getId(),
                personal.getInternalMetadataId(), personal.getTitle(), null, null
        );
    }

    private List<RegistrationResult> registerResolved(long userId, ModspaceType modspace,
                                                      Collection<String> originalIds, boolean autoBackup,
                                                      Long dependencyOf) {
        Map<String, Long> internalIds = new LinkedHashMap<>();
        Set<String> toFetch = new LinkedHashSet<>();
        for (String originalId : new LinkedHashSet<>(originalIds)) {
            Optional<ItemMetadataInternal> internal = internalService.find(modspace, originalId);
            if (internal.isPresent() && internal.get().getLifecycleState() == ItemLifecycleState.ACTIVE) {
                internalIds.put(originalId, internal.get().getId());
            } else {
                internalIds.put(originalId, null);
                toFetch.add(originalId);
            }
        }

        String fetchError = null;
        if (!toFetch.isEmpty()) {
            try {
                for (SyncOutcome outcome : sourceFetchService.sync(modspace, toFetch)) {
                    internalIds.put(outcome.originalId(), outcome.internalId());
                }
            } catch (ApiException e) {
                log.warn("Sync with {} failed during registration: {}", modspace, e.getMessage());
                fetchError = e.getCode();
            }
        }

        List<RegistrationResult> results = new ArrayList<>();
        for (Map.Entry<String, Long> entry : internalIds.entrySet()) {
            if (fetchError != null && toFetch.contains(entry.getKey())) {
                results.add(result(
                        modspace, entry.getKey(), Status.FAILED, null, null, null, fetchError,
                        dependencyOf
                ));
            } else {
                results.add(attach(userId, modspace, entry.getKey(), entry.getValue(), autoBackup, dependencyOf));
            }
        }
        return results;
    }

    private RegistrationResult attach(long userId, ModspaceType modspace, String originalId, long internalId,
                                      boolean autoBackup, Long dependencyOf) {
        Optional<ItemMetadataInternal> current = internalService.find(internalId);
        if (current.isEmpty()) {
            return result(
                    modspace, originalId, Status.FAILED, null, internalId, null,
                    "ITEM_BEING_DELETED", dependencyOf
            );
        }
        String title = projectionService.find(internalId).map(ItemMetadataProjection::getTitle).orElse(null);
        String reason = unavailableReason(current.get().getSourceStatus() == ItemSourceStatus.UNAVAILABLE, title);
        try {
            Attachment attachment = transactionTemplate.execute(status -> {
                Optional<ItemMetadataPersonal> existing = personalService.findOfInternal(userId, internalId);
                if (existing.isPresent()) {
                    return new Attachment(existing.get(), false);
                }
                return new Attachment(
                        personalService.save(
                                new ItemMetadataPersonal(userId, internalId, null, null, null, autoBackup)), true
                );
            });
            return result(
                    modspace, originalId,
                    attachment.created() ? Status.REGISTERED : Status.ALREADY_REGISTERED,
                    attachment.personal().getId(), internalId, title, reason, dependencyOf
            );
        } catch (ApiException e) {
            return result(
                    modspace, originalId, Status.FAILED, null, internalId, title, e.getCode(),
                    dependencyOf
            );
        } catch (DataIntegrityViolationException _) {
            // The same user registered the same item concurrently
            return personalService.findOfInternal(userId, internalId)
                    .map(p -> result(
                            modspace, originalId, Status.ALREADY_REGISTERED, p.getId(), internalId,
                            title, reason, dependencyOf
                    ))
                    .orElseGet(() -> result(
                            modspace, originalId, Status.FAILED, null, internalId, title,
                            "CONCURRENT_CHANGE", dependencyOf
                    ));
        }
    }

    private record Attachment(ItemMetadataPersonal personal, boolean created) {
    }
}
