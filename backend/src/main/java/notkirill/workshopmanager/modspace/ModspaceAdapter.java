package notkirill.workshopmanager.modspace;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface ModspaceAdapter {
    ModspaceType type();

    boolean isValidOriginalId(String originalId);

    <R> List<R> sync(Collection<String> originalIds, Function<SourceItem, R> store);

    List<String> getDependencies(String originalId);

    void deleteModspaceData(String originalId);

    default boolean supportsAuthorNames() {
        return false;
    }

    default Map<String, String> fetchAuthorNames(Collection<String> originalIds) {
        return Map.of();
    }

    default boolean supportsGameNames() {
        return false;
    }

    default Optional<String> fetchGameName(String externalId) {
        return Optional.empty();
    }
}
