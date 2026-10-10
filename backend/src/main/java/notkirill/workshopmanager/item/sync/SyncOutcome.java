package notkirill.workshopmanager.item.sync;

public record SyncOutcome(
        String originalId,
        SyncStatus status,
        long internalId,
        boolean sourceChanged,
        boolean dependenciesChanged
) {
}
