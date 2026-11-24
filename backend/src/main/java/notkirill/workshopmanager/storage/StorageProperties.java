package notkirill.workshopmanager.storage;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@ConfigurationProperties("application.storage")
@Validated
public record StorageProperties(
        @Name("s3") @Valid S3 s3
) {
    public StorageProperties {
        if (s3 == null) {
            s3 = new S3(null, null, null, null, null, null);
        }
    }

    public record S3(
            @Name("endpoint") URI endpoint,
            @Name("region") String region,
            @Name("bucket") String bucket,
            @Name("access-key") String accessKey,
            @Name("secret-key") String secretKey,
            @Name("create-bucket") Boolean createBucket
    ) {
        public S3 {
            if (region == null || region.isBlank()) {
                region = "us-east-1";
            }
            if (bucket == null || bucket.isBlank()) {
                bucket = "workshop-manager";
            }
            if (createBucket == null) {
                createBucket = true;
            }
        }
    }
}
