package com.relativesHelp.relativesHelp.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Slf4j
public class MinIOConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.secure:false}")
    private boolean secure;

    @Bean
    public MinioClient minioClient() {
        try {
            // Parse endpoint to extract host and port
            URI uri = new URI(endpoint);
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? (uri.getScheme().equals("https") ? 443 : 80) : uri.getPort();
            boolean useHttps = uri.getScheme().equals("https");
            
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(host, port, useHttps)
                    .credentials(accessKey, secretKey)
                    .build();

            // Kiểm tra và tạo bucket nếu chưa tồn tại
            initializeBucket(minioClient);

            log.info("MinIO client initialized successfully. Endpoint: {}, Bucket: {}", endpoint, bucketName);
            return minioClient;
        } catch (URISyntaxException e) {
            log.error("Invalid MinIO endpoint: {}", endpoint, e);
            throw new IllegalStateException("Failed to initialize MinIO client", e);
        }
    }

    private void initializeBucket(MinioClient minioClient) {
        try {
            boolean found = minioClient.bucketExists(
                    io.minio.BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!found) {
                minioClient.makeBucket(
                        io.minio.MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("Created MinIO bucket: {}", bucketName);
            } else {
                log.info("MinIO bucket already exists: {}", bucketName);
            }
            
            // Set bucket policy for public read access
            try {
                String policy = "{\n" +
                    "  \"Version\": \"2012-10-17\",\n" +
                    "  \"Statement\": [\n" +
                    "    {\n" +
                    "      \"Effect\": \"Allow\",\n" +
                    "      \"Principal\": {\"AWS\": [\"*\"]},\n" +
                    "      \"Action\": [\"s3:GetObject\"],\n" +
                    "      \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
                
                minioClient.setBucketPolicy(
                    io.minio.SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(policy)
                            .build()
                );
                log.info("Set bucket policy for public read access on bucket: {}", bucketName);
            } catch (Exception e) {
                // Policy might already be set or there's an error, log but don't fail
                log.warn("Failed to set bucket policy (may already be set): {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("Failed to initialize MinIO bucket: {}", bucketName, e);
            throw new IllegalStateException("Failed to initialize MinIO bucket", e);
        }
    }

    @Bean
    public String minioBucketName() {
        return bucketName;
    }
}

