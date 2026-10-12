package notkirill.workshopmanager.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.nio.file.Path;

public class S3ObjectStorage implements ObjectStorage, AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(S3ObjectStorage.class);

    private final S3Client client;
    private final String bucket;

    public S3ObjectStorage(StorageProperties.S3 properties) {
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(properties.region()))
                // Self-hosted S3-compatible servers address buckets by path, not by host
                .forcePathStyle(true);
        if (properties.endpoint() != null) {
            builder.endpointOverride(properties.endpoint());
        }
        if (properties.accessKey() != null && !properties.accessKey().isBlank()) {
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(properties.accessKey(), properties.secretKey())));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.builder().build());
        }
        this.client = builder.build();
        this.bucket = properties.bucket();
        if (properties.createBucket()) {
            ensureBucket();
        }
    }

    private void ensureBucket() {
        try {
            client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException _) {
            client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            log.info("Created S3 bucket '{}'", bucket);
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
                log.info("Created S3 bucket '{}'", bucket);
            } else {
                log.warn("Could not verify S3 bucket '{}': {}", bucket, e.getMessage());
            }
        } catch (RuntimeException e) {
            log.warn("Could not verify S3 bucket '{}': {}", bucket, e.getMessage());
        }
    }

    @Override
    public void put(String key, Path file, String contentType) {
        client.putObject(
                PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType).build(),
                RequestBody.fromFile(file)
        );
    }

    @Override
    public void put(String key, InputStream data, long size, String contentType) {
        client.putObject(
                PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType).build(),
                RequestBody.fromInputStream(data, size)
        );
    }

    @Override
    public InputStream get(String key) {
        return client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build());
    }

    @Override
    public boolean exists(String key) {
        try {
            client.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public void delete(String key) {
        client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }

    @Override
    public void close() {
        client.close();
    }
}
