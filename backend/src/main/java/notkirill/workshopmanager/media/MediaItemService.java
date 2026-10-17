package notkirill.workshopmanager.media;

import jakarta.annotation.Nullable;
import notkirill.workshopmanager.common.FileNames;
import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.exception.NotFoundException;
import notkirill.workshopmanager.media.enums.MediaProvider;
import notkirill.workshopmanager.media.enums.MediaType;
import notkirill.workshopmanager.media.models.MediaEmbed;
import notkirill.workshopmanager.media.models.MediaFile;
import notkirill.workshopmanager.media.models.MediaItem;
import notkirill.workshopmanager.media.repositories.MediaEmbedRepository;
import notkirill.workshopmanager.media.repositories.MediaFileRepository;
import notkirill.workshopmanager.media.repositories.MediaItemRepository;
import notkirill.workshopmanager.media.source.SourceMedia;
import notkirill.workshopmanager.security.SignedUrlService;
import notkirill.workshopmanager.storage.ObjectStorage;
import notkirill.workshopmanager.storage.StoredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MediaItemService {
    private static final Logger log = LoggerFactory.getLogger(MediaItemService.class);

    private static final Pattern YOUTUBE = Pattern.compile(
            "(?:youtube\\.com/(?:watch\\?(?:.*&)?v=|shorts/|embed/)|youtu\\.be/)([A-Za-z0-9_-]{11})");
    private static final Pattern SKETCHFAB = Pattern.compile(
            "sketchfab\\.com/(?:3d-models/(?:[^/?#]*-)?|models/)([0-9a-f]{32})");

    private final MediaItemRepository itemRepository;
    private final MediaFileRepository fileRepository;
    private final MediaEmbedRepository embedRepository;
    private final ObjectStorage storage;
    private final SignedUrlService signedUrlService;
    private final MediaProperties properties;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public MediaItemService(MediaItemRepository itemRepository, MediaFileRepository fileRepository,
                            MediaEmbedRepository embedRepository, ObjectStorage storage,
                            SignedUrlService signedUrlService, MediaProperties properties,
                            PlatformTransactionManager transactionManager) {
        this.itemRepository = itemRepository;
        this.fileRepository = fileRepository;
        this.embedRepository = embedRepository;
        this.storage = storage;
        this.signedUrlService = signedUrlService;
        this.properties = properties;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    private static Optional<ImageFormat> detectFormat(InputStreamSource data) {
        try (InputStream in = data.getInputStream()) {
            return ImageFormat.detect(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read uploaded image", e);
        }
    }

    private static String fileName(String originalName, ImageFormat format) {
        String name = FileNames.sanitize(originalName, "image");
        int dot = name.lastIndexOf('.');
        String base = dot >= 0 ? name.substring(0, dot).strip() : name;
        return (base.isEmpty() ? "image" : base) + "." + format.extension();
    }

    private static String embedUrl(MediaEmbed embed) {
        return switch (embed.getProvider()) {
            case YOUTUBE -> "https://www.youtube-nocookie.com/embed/" + embed.getExternalId();
            case SKETCHFAB -> "https://sketchfab.com/models/" + embed.getExternalId() + "/embed";
            case UNKNOWN -> null;
        };
    }

    private static String resource(long mediaItemId) {
        return "media:" + mediaItemId;
    }

    public MediaItem createImage(long ownerUserId, ImageUpload upload) {
        if (upload.size() == 0) {
            throw new BadRequestException("EMPTY_UPLOAD", "Uploaded file is empty");
        }
        if (upload.size() > properties.maxImageSize().toBytes()) {
            throw new BadRequestException(
                    "UPLOAD_TOO_LARGE",
                    "Images are limited to " + properties.maxImageSize().toMegabytes() + " MB"
            );
        }
        ImageFormat format = detectFormat(upload.data())
                .orElseThrow(() -> new BadRequestException(
                        "UNSUPPORTED_MEDIA",
                        "Only PNG, JPEG, GIF, WebP and AVIF images are allowed"
                ));
        return storeImage(
                ownerUserId, format, fileName(upload.fileName(), format), upload.size(), upload.data(),
                null
        );
    }

    public MediaItem createSourceImage(byte[] data, String originalName, ImageFormat format, String sourceVersion) {
        return storeImage(
                null, format, fileName(originalName, format), data.length, new ByteArrayResource(data),
                sourceVersion
        );
    }

    private MediaItem storeImage(@Nullable Long ownerUserId, ImageFormat format, String fileName, long size,
                                 InputStreamSource data, @Nullable String sourceVersion) {
        MediaItem item = itemRepository.save(new MediaItem(MediaType.FILE, ownerUserId));
        String key = "media/" + item.getId() + "/" + fileName;
        try {
            try (InputStream in = data.getInputStream()) {
                storage.put(key, in, size, format.mimeType());
            } catch (IOException e) {
                throw new UncheckedIOException("Cannot store image", e);
            }
            transactionTemplate.executeWithoutResult(status ->
                                                             fileRepository.save(
                                                                     new MediaFile(
                                                                             item.getId(), sourceVersion, size,
                                                                             fileName, key
                                                                     )));
            return item;
        } catch (RuntimeException e) {
            try {
                storage.delete(key);
            } catch (RuntimeException deleteError) {
                log.warn("Could not remove orphan media object {}: {}", key, deleteError.getMessage());
            }
            try {
                itemRepository.deleteById(item.getId());
            } catch (RuntimeException deleteError) {
                log.warn(
                        "Could not remove media item {} of a failed upload: {}", item.getId(),
                        deleteError.getMessage()
                );
            }
            throw e;
        }
    }

    @Transactional
    public MediaItem createEmbed(long ownerUserId, String url) {
        MediaProvider provider;
        String externalId;
        Matcher youtube = YOUTUBE.matcher(url);
        Matcher sketchfab = SKETCHFAB.matcher(url);
        if (youtube.find()) {
            provider = MediaProvider.YOUTUBE;
            externalId = youtube.group(1);
        } else if (sketchfab.find()) {
            provider = MediaProvider.SKETCHFAB;
            externalId = sketchfab.group(1);
        } else {
            throw new BadRequestException("UNSUPPORTED_EMBED", "Only YouTube and Sketchfab links are supported");
        }
        return createEmbed(ownerUserId, provider, externalId);
    }

    @Transactional
    public MediaItem createEmbed(@Nullable Long ownerUserId, MediaProvider provider, String externalId) {
        MediaItem item = itemRepository.save(new MediaItem(MediaType.EMBED, ownerUserId));
        embedRepository.save(new MediaEmbed(item.getId(), externalId, provider));
        return item;
    }

    @Transactional(readOnly = true)
    public Optional<MediaItem> find(long mediaItemId) {
        return itemRepository.findById(mediaItemId);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> sourceVersions(Collection<Long> mediaItemIds) {
        Map<Long, String> versions = new HashMap<>();
        for (MediaFile file : fileRepository.findAllById(mediaItemIds)) {
            if (file.getSourceVersion() != null) {
                versions.put(file.getMediaItemId(), file.getSourceVersion());
            }
        }
        for (MediaEmbed embed : embedRepository.findAllById(mediaItemIds)) {
            versions.put(embed.getMediaItemId(), SourceMedia.Embed.version(embed.getProvider(), embed.getExternalId()));
        }
        return versions;
    }

    @Transactional(readOnly = true)
    public Map<Long, MediaItemDetails> describe(Collection<Long> mediaItemIds) {
        Map<Long, MediaFile> files = fileRepository.findAllById(mediaItemIds).stream()
                .collect(Collectors.toMap(MediaFile::getMediaItemId, Function.identity()));
        Map<Long, MediaEmbed> embeds = embedRepository.findAllById(mediaItemIds).stream()
                .collect(Collectors.toMap(MediaEmbed::getMediaItemId, Function.identity()));

        Map<Long, MediaItemDetails> result = new HashMap<>();
        for (MediaItem item : itemRepository.findAllById(mediaItemIds)) {
            if (item.getType() == MediaType.FILE) {
                MediaFile file = files.get(item.getId());
                SignedUrlService.Signature signature = signedUrlService.signCacheable(
                        resource(item.getId()),
                        properties.downloadLinkTtl()
                );
                String url = "/api/public/downloads/media/" + item.getId()
                        + "?exp=" + signature.expiresAt() + "&sig=" + signature.value();
                result.put(
                        item.getId(), new MediaItemDetails(
                                item.getId(), MediaType.FILE, url, null, null, file.getFileName()
                        )
                );
            } else {
                MediaEmbed embed = embeds.get(item.getId());
                result.put(
                        item.getId(), new MediaItemDetails(
                                item.getId(), MediaType.EMBED, embedUrl(embed), embed.getProvider(),
                                embed.getExternalId(),
                                null
                        )
                );
            }
        }
        return result;
    }

    @Transactional
    public void delete(long mediaItemId) {
        fileRepository.findById(mediaItemId).ifPresent(file -> {
            fileRepository.delete(file);
            deleteObjectAfterCommit(file.getS3Key());
        });
        embedRepository.findById(mediaItemId).ifPresent(embedRepository::delete);
        itemRepository.findById(mediaItemId).ifPresent(itemRepository::delete);
    }

    @Transactional(readOnly = true)
    public StoredObject openFile(long mediaItemId, long expiresAt, String signature) {
        if (!signedUrlService.verify(resource(mediaItemId), expiresAt, signature)) {
            throw new NotFoundException("LINK_EXPIRED", "Media link is invalid or expired");
        }
        MediaFile file = fileRepository.findById(mediaItemId)
                .orElseThrow(() -> new NotFoundException("MEDIA_NOT_FOUND", "Media not found"));
        ImageFormat format = ImageFormat.fromFileName(file.getFileName())
                .orElseThrow(() -> new IllegalStateException("Media file without image extension: "
                                                                     + file.getMediaItemId()));
        return new StoredObject(
                storage.get(file.getS3Key()), file.getFileName(), format.mimeType(),
                file.getFileSize()
        );
    }

    private void deleteObjectAfterCommit(String key) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    storage.delete(key);
                } catch (RuntimeException e) {
                    log.warn("Could not delete media object {}: {}", key, e.getMessage());
                }
            }
        });
    }
}
