package notkirill.workshopmanager.content;

import jakarta.annotation.Nullable;
import notkirill.workshopmanager.content.models.ContentUpload;
import notkirill.workshopmanager.content.models.ItemContentRevision;
import notkirill.workshopmanager.content.repositories.ContentUploadRepository;
import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.exception.ConflictException;
import notkirill.workshopmanager.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ContentUploadService {
    private static final Logger log = LoggerFactory.getLogger(ContentUploadService.class);

    private static final int BUFFER_SIZE = 64 * 1024;

    private final ContentUploadRepository repository;
    private final ContentRevisionService revisionService;
    private final ContentProperties properties;
    private final Clock clock;
    private final TransactionTemplate transactionTemplate;
    private final Set<Long> busy = ConcurrentHashMap.newKeySet();

    @Autowired
    public ContentUploadService(ContentUploadRepository repository, ContentRevisionService revisionService,
                                ContentProperties properties, Clock clock,
                                PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.revisionService = revisionService;
        this.properties = properties;
        this.clock = clock;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public long countOpen() {
        return repository.count();
    }

    public long chunkSize() {
        return properties.uploads().chunkSize().toBytes();
    }

    public ContentUpload start(long userId, long internalId, String fileName, long size) {
        if (size <= 0) {
            throw new BadRequestException("EMPTY_UPLOAD", "Uploaded file is empty");
        }
        if (size > properties.uploads().maxSize().toBytes()) {
            throw new BadRequestException(
                    "UPLOAD_TOO_LARGE",
                    "Backups are limited to " + properties.uploads().maxSize().toGigabytes() + " GB"
            );
        }
        return repository.save(
                new ContentUpload(internalId, userId, ContentRevisionService.sanitizeFileName(fileName), size));
    }

    public ContentUpload require(long userId, long internalId, long uploadId) {
        return repository.findByIdAndUserIdAndInternalMetadataId(uploadId, userId, internalId)
                .orElseThrow(() -> new NotFoundException("UPLOAD_NOT_FOUND", "Upload not found"));
    }

    public UploadProgress append(long userId, long internalId, long uploadId, long offset, long length,
                                 InputStream body) {
        if (!busy.add(uploadId)) {
            throw new ConflictException("UPLOAD_BUSY", "Another request is writing to this upload");
        }
        try {
            ContentUpload upload = require(userId, internalId, uploadId);
            if (offset != upload.getReceived()) {
                throw new ConflictException("UPLOAD_OFFSET_MISMATCH", "The upload continues at another offset");
            }
            if (length < 0) {
                throw new BadRequestException("UPLOAD_LENGTH_REQUIRED", "The part needs a Content-Length");
            }
            if (length > properties.uploads().chunkSize().toBytes()) {
                throw new BadRequestException("UPLOAD_PART_TOO_LARGE", "The part is larger than allowed");
            }
            if (offset + length > upload.getSize()) {
                throw new BadRequestException("UPLOAD_PART_TOO_LARGE", "The part goes beyond the file size");
            }

            long received = write(upload, offset, length, body);
            if (received < upload.getSize()) {
                return new UploadProgress(upload, received, null);
            }
            ItemContentRevision revision = revisionService.storeRevision(
                    internalId, partFile(upload.getId()),
                    upload.getFileName(), null, userId
            );
            delete(upload.getId());
            return new UploadProgress(upload, received, revision);
        } finally {
            busy.remove(uploadId);
        }
    }

    public void cancel(long userId, long internalId, long uploadId) {
        if (!busy.add(uploadId)) {
            throw new ConflictException("UPLOAD_BUSY", "Another request is writing to this upload");
        }
        try {
            require(userId, internalId, uploadId);
            delete(uploadId);
        } finally {
            busy.remove(uploadId);
        }
    }

    public int deleteAbandoned() {
        OffsetDateTime before = OffsetDateTime.now(clock).minus(properties.uploads().expiry());
        int deleted = 0;
        for (ContentUpload upload : repository.findByTimeUpdatedBefore(before)) {
            if (busy.add(upload.getId())) {
                try {
                    delete(upload.getId());
                    deleted++;
                } finally {
                    busy.remove(upload.getId());
                }
            }
        }
        return deleted;
    }

    private long write(ContentUpload upload, long offset, long length, InputStream body) {
        Path file = partFile(upload.getId());
        long written = 0;
        try {
            Files.createDirectories(file.getParent());
            try (FileChannel channel = FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                channel.truncate(offset);
                channel.position(offset);
                byte[] buffer = new byte[BUFFER_SIZE];
                while (written < length) {
                    int read;
                    try {
                        read = body.read(buffer, 0, (int) Math.min(buffer.length, length - written));
                    } catch (IOException e) {
                        log.info(
                                "Upload {} interrupted after {} of {} bytes of a part", upload.getId(), written,
                                length
                        );
                        break;
                    }
                    if (read < 0) {
                        break;
                    }
                    ByteBuffer chunk = ByteBuffer.wrap(buffer, 0, read);
                    while (chunk.hasRemaining()) {
                        channel.write(chunk);
                    }
                    written += read;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write upload part", e);
        } finally {
            long received = offset + written;
            transactionTemplate.executeWithoutResult(status ->
                                                             repository.findById(upload.getId()).ifPresent(
                                                                     found -> found.setReceived(received)));
        }
        return offset + written;
    }

    private void delete(long uploadId) {
        try {
            Files.deleteIfExists(partFile(uploadId));
        } catch (IOException e) {
            log.warn("Could not delete upload file {}: {}", partFile(uploadId), e.getMessage());
        }
        repository.deleteById(uploadId);
    }

    private Path partFile(long uploadId) {
        return properties.tempDir().resolve("uploads").resolve(uploadId + ".part");
    }

    public record UploadProgress(ContentUpload upload, long received, @Nullable ItemContentRevision revision) {
    }
}
