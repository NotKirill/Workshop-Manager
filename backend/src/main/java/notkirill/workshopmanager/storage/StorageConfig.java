package notkirill.workshopmanager.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {
    private static final Logger log = LoggerFactory.getLogger(StorageConfig.class);

    @Bean
    public ObjectStorage objectStorage(StorageProperties properties) {
        return switch (properties.type()) {
            case S3 -> {
                log.info("Using S3 object storage, bucket '{}'", properties.s3().bucket());
                yield new S3ObjectStorage(properties.s3());
            }
            case FILESYSTEM -> {
                log.info("Using filesystem object storage at {}", properties.filesystem().root());
                yield new FilesystemObjectStorage(properties.filesystem().root());
            }
        };
    }
}
