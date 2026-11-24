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
        log.info("Using S3 object storage, bucket '{}'", properties.s3().bucket());
        return new S3ObjectStorage(properties.s3());
    }
}
