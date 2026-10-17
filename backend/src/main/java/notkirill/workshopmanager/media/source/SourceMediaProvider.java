package notkirill.workshopmanager.media.source;

import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.modspace.ModspaceType;

import java.net.URI;
import java.util.List;
import java.util.Set;

public interface SourceMediaProvider {
    ModspaceType modspace();

    List<SourceMedia> describe(EntityType entity, String originalId);

    Set<String> downloadHosts();

    default SourceMediaDownloader.DownloadedFile download(URI url, SourceMediaDownloader downloader) {
        return downloader.download(url, downloadHosts());
    }
}
