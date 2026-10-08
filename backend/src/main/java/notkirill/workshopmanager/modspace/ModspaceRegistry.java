package notkirill.workshopmanager.modspace;

import notkirill.workshopmanager.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ModspaceRegistry {
    private final Map<ModspaceType, ModspaceAdapter> adapters;

    @Autowired
    public ModspaceRegistry(List<ModspaceAdapter> adapters) {
        Map<ModspaceType, ModspaceAdapter> map = new EnumMap<>(ModspaceType.class);
        for (ModspaceAdapter adapter : adapters) {
            map.put(adapter.type(), adapter);
        }
        this.adapters = Collections.unmodifiableMap(map);
    }

    public ModspaceAdapter get(ModspaceType type) {
        ModspaceAdapter adapter = adapters.get(type);
        if (adapter == null) {
            throw new BadRequestException("UNSUPPORTED_MODSPACE", "Modspace is not supported: " + type);
        }
        return adapter;
    }

    public Optional<ModspaceAdapter> find(ModspaceType type) {
        return Optional.ofNullable(adapters.get(type));
    }

    public Collection<ModspaceAdapter> all() {
        return adapters.values();
    }
}
