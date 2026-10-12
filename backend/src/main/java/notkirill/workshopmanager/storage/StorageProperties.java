package notkirill.workshopmanager.storage;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.nio.file.Path;

@ConfigurationProperties("application.storage")
@Validated
public record StorageProperties(
        @Name("type") Type type,
        @Name("filesystem") @Valid Filesystem filesystem,
        @Name("s3") @Valid S3 s3
) {
    public StorageProperties {
        if (type == null) {
            type = Type.FILESYSTEM;
        }
        if (filesystem == null) {
            filesystem = new Filesystem(null);
        }
        if (s3 == null) {
            s3 = new S3(null, null, null, null, null, null);
        }
    }

    public enum Type {
        FILESYSTEM, S3
    }

    public record Filesystem(@Name("root") Path root) {
        public Filesystem {
            if (root == null) {
                root = Path.of(System.getProperty("user.home"), ".workshop-manager", "storage");
            }
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
