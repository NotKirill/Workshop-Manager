package notkirill.workshopmanager.item.library.search;

import notkirill.workshopmanager.modspace.ModspaceType;

import java.util.List;

public interface ModspaceSearchFields {
    ModspaceType modspace();

    List<SearchField> fields();
}
