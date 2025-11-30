package com.relativesHelp.relativesHelp.media.service;

import com.relativesHelp.relativesHelp.media.dto.MinIOFileInfo;
import com.relativesHelp.relativesHelp.media.dto.MinIOPresignedUrlResponse;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import io.minio.messages.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinIOService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * Upload file to MinIO
     * @param file MultipartFile to upload
     * @param folderPath Folder path in bucket (e.g., "family/123" or "users/456")
     * @return URL of uploaded file
     */
    public String uploadFile(MultipartFile file, String folderPath) {
        try {
            String fileName = generateFileName(file.getOriginalFilename());
            String objectName = folderPath != null && !folderPath.isEmpty() 
                    ? folderPath + "/" + fileName 
                    : fileName;

            // Upload file
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // Set object as public (readable by anyone)
            try {
                setObjectPublic(objectName);
            } catch (Exception e) {
                log.warn("Failed to set object as public, but file uploaded: {}", objectName, e);
            }

            // Generate URL
            String fileUrl = generateFileUrl(objectName);
            log.info("File uploaded successfully to MinIO: {}", fileUrl);
            return fileUrl;

        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to upload file to MinIO", e);
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    /**
     * Download file from MinIO
     * @param objectName Object name in bucket
     * @return InputStream of the file
     */
    public InputStream downloadFile(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to download file from MinIO: {}", objectName, e);
            throw new RuntimeException("Failed to download file from MinIO", e);
        }
    }

    /**
     * Delete file from MinIO
     * @param objectName Object name in bucket
     */
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("File deleted successfully from MinIO: {}", objectName);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to delete file from MinIO: {}", objectName, e);
            throw new RuntimeException("Failed to delete file from MinIO", e);
        }
    }

    /**
     * Check if file exists in MinIO
     * @param objectName Object name in bucket
     * @return true if file exists
     */
    public boolean fileExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            log.error("Error checking file existence in MinIO: {}", objectName, e);
            throw new RuntimeException("Error checking file existence in MinIO", e);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to check file existence in MinIO: {}", objectName, e);
            throw new RuntimeException("Failed to check file existence in MinIO", e);
        }
    }

    /**
     * Generate unique file name
     */
    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * Generate file URL
     */
    private String generateFileUrl(String objectName) {
        // Remove leading slash if present
        if (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        return endpoint + "/" + bucketName + "/" + objectName;
    }

    /**
     * Extract object name from URL
     */
    public String extractObjectNameFromUrl(String url) {
        String prefix = endpoint + "/" + bucketName + "/";
        if (url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }
        return url;
    }

    /**
     * Upload file with specific ID
     * @param file MultipartFile to upload
     * @param fileId ID to use for the file (e.g., media ID, user ID)
     * @param folderPath Optional folder path
     * @return URL of uploaded file
     */
    public String uploadFileById(MultipartFile file, String fileId, String folderPath) {
        try {
            String extension = getFileExtension(file.getOriginalFilename());
            String fileName = fileId + extension;
            String objectName = folderPath != null && !folderPath.isEmpty()
                    ? folderPath + "/" + fileName
                    : fileName;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            String fileUrl = generateFileUrl(objectName);
            log.info("File uploaded with ID {} to MinIO: {}", fileId, fileUrl);
            return fileUrl;

        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to upload file with ID {} to MinIO", fileId, e);
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    /**
     * Save file by ID (overwrites if exists)
     * @param fileId ID of the file
     * @param inputStream File input stream
     * @param contentType Content type
     * @param folderPath Optional folder path
     * @return URL of saved file
     */
    public String saveFileById(String fileId, InputStream inputStream, String contentType, long size, String folderPath) {
        try {
            String objectName = folderPath != null && !folderPath.isEmpty()
                    ? folderPath + "/" + fileId
                    : fileId;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );

            String fileUrl = generateFileUrl(objectName);
            log.info("File saved with ID {} to MinIO: {}", fileId, fileUrl);
            return fileUrl;

        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to save file with ID {} to MinIO", fileId, e);
            throw new RuntimeException("Failed to save file to MinIO", e);
        }
    }

    /**
     * Save file by ID from byte array
     * @param fileId ID of the file
     * @param data File data
     * @param contentType Content type
     * @param folderPath Optional folder path
     * @return URL of saved file
     */
    public String saveFileById(String fileId, byte[] data, String contentType, String folderPath) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            return saveFileById(fileId, inputStream, contentType, data.length, folderPath);
        } catch (IOException e) {
            log.error("Failed to save file with ID {} to MinIO", fileId, e);
            throw new RuntimeException("Failed to save file to MinIO", e);
        }
    }

    /**
     * Get file by ID
     * @param fileId ID of the file
     * @param folderPath Optional folder path
     * @return InputStream of the file
     */
    public InputStream getFileById(String fileId, String folderPath) {
        String objectName = folderPath != null && !folderPath.isEmpty()
                ? folderPath + "/" + fileId
                : fileId;
        return downloadFile(objectName);
    }

    /**
     * Get file URL by ID
     * @param fileId ID of the file
     * @param folderPath Optional folder path
     * @return URL of the file
     */
    public String getFileUrlById(String fileId, String folderPath) {
        String objectName = folderPath != null && !folderPath.isEmpty()
                ? folderPath + "/" + fileId
                : fileId;
        return generateFileUrl(objectName);
    }

    /**
     * Delete file by ID
     * @param fileId ID of the file
     * @param folderPath Optional folder path
     */
    public void deleteFileById(String fileId, String folderPath) {
        String objectName = folderPath != null && !folderPath.isEmpty()
                ? folderPath + "/" + fileId
                : fileId;
        deleteFile(objectName);
    }

    /**
     * Check if file exists by ID
     * @param fileId ID of the file
     * @param folderPath Optional folder path
     * @return true if file exists
     */
    public boolean fileExistsById(String fileId, String folderPath) {
        String objectName = folderPath != null && !folderPath.isEmpty()
                ? folderPath + "/" + fileId
                : fileId;
        return fileExists(objectName);
    }

    /**
     * Get file metadata/info by ID
     * @param fileId ID of the file
     * @param folderPath Optional folder path
     * @return MinIOFileInfo with file information
     */
    public MinIOFileInfo getFileInfoById(String fileId, String folderPath) {
        try {
            String objectName = folderPath != null && !folderPath.isEmpty()
                    ? folderPath + "/" + fileId
                    : fileId;

            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            return MinIOFileInfo.builder()
                    .objectName(objectName)
                    .size(stat.size())
                    .etag(stat.etag())
                    .contentType(stat.contentType())
                    .lastModified(stat.lastModified())
                    .url(generateFileUrl(objectName))
                    .build();
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to get file info for ID {} from MinIO", fileId, e);
            throw new RuntimeException("Failed to get file info from MinIO", e);
        }
    }

    /**
     * List all files in a folder/path
     * @param folderPath Folder path to list (null or empty for root)
     * @return List of file names/object names
     */
    public List<String> listFiles(String folderPath) {
        List<String> files = new ArrayList<>();
        try {
            String prefix = folderPath != null && !folderPath.isEmpty() ? folderPath + "/" : "";
            
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .recursive(false)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                if (!item.isDir()) {
                    files.add(item.objectName());
                }
            }

            log.info("Listed {} files from folder: {}", files.size(), folderPath);
            return files;

        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to list files from folder: {}", folderPath, e);
            throw new RuntimeException("Failed to list files from MinIO", e);
        }
    }

    /**
     * List all files recursively in a folder/path
     * @param folderPath Folder path to list (null or empty for root)
     * @return List of file names/object names
     */
    public List<String> listFilesRecursive(String folderPath) {
        List<String> files = new ArrayList<>();
        try {
            String prefix = folderPath != null && !folderPath.isEmpty() ? folderPath + "/" : "";
            
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                if (!item.isDir()) {
                    files.add(item.objectName());
                }
            }

            log.info("Listed {} files recursively from folder: {}", files.size(), folderPath);
            return files;

        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to list files recursively from folder: {}", folderPath, e);
            throw new RuntimeException("Failed to list files from MinIO", e);
        }
    }

    /**
     * Copy file from source to destination
     * @param sourceObjectName Source object name
     * @param destObjectName Destination object name
     */
    public void copyFile(String sourceObjectName, String destObjectName) {
        try {
            CopySource source = CopySource.builder()
                    .bucket(bucketName)
                    .object(sourceObjectName)
                    .build();

            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(destObjectName)
                            .source(source)
                            .build()
            );

            log.info("File copied from {} to {}", sourceObjectName, destObjectName);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to copy file from {} to {}", sourceObjectName, destObjectName, e);
            throw new RuntimeException("Failed to copy file in MinIO", e);
        }
    }

    /**
     * Move file from source to destination (copy then delete)
     * @param sourceObjectName Source object name
     * @param destObjectName Destination object name
     */
    public void moveFile(String sourceObjectName, String destObjectName) {
        copyFile(sourceObjectName, destObjectName);
        deleteFile(sourceObjectName);
        log.info("File moved from {} to {}", sourceObjectName, destObjectName);
    }

    /**
     * Create folder structure (MinIO doesn't have folders, but we can create empty objects)
     * @param folderPath Folder path to create
     */
    public void createFolder(String folderPath) {
        try {
            String folderObjectName = folderPath.endsWith("/") ? folderPath : folderPath + "/";
            
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(folderObjectName)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .contentType("application/x-directory")
                            .build()
            );

            log.info("Folder created: {}", folderPath);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to create folder: {}", folderPath, e);
            throw new RuntimeException("Failed to create folder in MinIO", e);
        }
    }

    /**
     * List folders/subfolders in a path
     * @param folderPath Folder path to list
     * @return List of folder names
     */
    public List<String> listFolders(String folderPath) {
        List<String> folders = new ArrayList<>();
        try {
            String prefix = folderPath != null && !folderPath.isEmpty() ? folderPath + "/" : "";
            
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .recursive(false)
                            .build()
            );

            String currentFolder = null;
            for (Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();
                
                // Extract folder name from object name
                if (objectName.startsWith(prefix)) {
                    String relativePath = objectName.substring(prefix.length());
                    if (relativePath.contains("/")) {
                        String folderName = relativePath.substring(0, relativePath.indexOf("/"));
                        String fullFolderPath = prefix.isEmpty() ? folderName : prefix + folderName;
                        if (!folders.contains(fullFolderPath) && !fullFolderPath.equals(currentFolder)) {
                            folders.add(fullFolderPath);
                            currentFolder = fullFolderPath;
                        }
                    }
                }
            }

            log.info("Listed {} folders from path: {}", folders.size(), folderPath);
            return folders;

        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to list folders from path: {}", folderPath, e);
            throw new RuntimeException("Failed to list folders from MinIO", e);
        }
    }

    /**
     * Generate presigned URL for file access (with expiration)
     * @param objectName Object name in bucket
     * @param expirationSeconds Expiration time in seconds (default: 1 hour)
     * @param method HTTP method (GET, PUT, POST)
     * @return Presigned URL response
     */
    public MinIOPresignedUrlResponse generatePresignedUrl(String objectName, int expirationSeconds, Method method) {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(method)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expirationSeconds)
                            .build()
            );

            ZonedDateTime expirationTime = ZonedDateTime.now().plusSeconds(expirationSeconds);

            return MinIOPresignedUrlResponse.builder()
                    .objectName(objectName)
                    .presignedUrl(url)
                    .expirationTime(expirationTime)
                    .expirationSeconds(expirationSeconds)
                    .method(method.name())
                    .build();
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to generate presigned URL for: {}", objectName, e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    /**
     * Generate presigned URL for GET (download) - default 1 hour
     */
    public MinIOPresignedUrlResponse generatePresignedDownloadUrl(String objectName) {
        return generatePresignedUrl(objectName, 3600, Method.GET);
    }

    /**
     * Generate presigned URL for PUT (upload) - default 1 hour
     */
    public MinIOPresignedUrlResponse generatePresignedUploadUrl(String objectName) {
        return generatePresignedUrl(objectName, 3600, Method.PUT);
    }

    /**
     * Validate if file is an image
     */
    public boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    /**
     * Validate if file is a video
     */
    public boolean isVideo(String contentType) {
        return contentType != null && contentType.startsWith("video/");
    }

    /**
     * Validate file size
     * @param fileSize File size in bytes
     * @param maxSizeMB Maximum size in MB
     * @return true if file size is valid
     */
    public boolean validateFileSize(long fileSize, int maxSizeMB) {
        long maxSizeBytes = maxSizeMB * 1024L * 1024L;
        return fileSize <= maxSizeBytes;
    }

    /**
     * Validate content type for images
     */
    public boolean validateImageContentType(String contentType) {
        if (contentType == null) return false;
        String[] allowedTypes = {
            "image/jpeg", "image/jpg", "image/png", "image/gif",
            "image/webp", "image/bmp", "image/svg+xml"
        };
        for (String type : allowedTypes) {
            if (type.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate content type for videos
     */
    public boolean validateVideoContentType(String contentType) {
        if (contentType == null) return false;
        String[] allowedTypes = {
            "video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo",
            "video/webm", "video/x-matroska", "video/3gpp"
        };
        for (String type : allowedTypes) {
            if (type.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set object metadata/tags
     */
    public void setObjectMetadata(String objectName, Map<String, String> metadata) {
        try {
            Map<String, String> tags = new HashMap<>();
            if (metadata != null) {
                tags.putAll(metadata);
            }

            minioClient.setObjectTags(
                    SetObjectTagsArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .tags(tags)
                            .build()
            );

            log.info("Set metadata for object: {}", objectName);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to set metadata for object: {}", objectName, e);
            throw new RuntimeException("Failed to set object metadata", e);
        }
    }

    /**
     * Get object metadata/tags
     */
    public Map<String, String> getObjectMetadata(String objectName) {
        try {
            Tags tags = minioClient.getObjectTags(
                    GetObjectTagsArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            return tags.get();
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to get metadata for object: {}", objectName, e);
            return new HashMap<>();
        }
    }

    /**
     * Set object as public (readable by anyone)
     * Note: This requires bucket policy to be set for public read access
     */
    public void setObjectPublic(String objectName) {
        try {
            // Try to set bucket policy for public read if not already set
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
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(policy)
                            .build()
                );
                log.info("Set bucket policy for public read access");
            } catch (Exception e) {
                // Policy might already be set, ignore
                log.debug("Bucket policy may already be set: {}", e.getMessage());
            }
            
            log.info("Object should be publicly accessible: {}", objectName);
        } catch (Exception e) {
            log.warn("Failed to set object as public (may need bucket policy): {}", objectName, e);
        }
    }

    /**
     * Batch upload files
     */
    public List<String> batchUploadFiles(List<MultipartFile> files, String folderPath) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String url = uploadFile(file, folderPath);
                urls.add(url);
            } catch (Exception e) {
                log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            }
        }
        return urls;
    }

    /**
     * Batch delete files
     */
    public void batchDeleteFiles(List<String> objectNames) {
        for (String objectName : objectNames) {
            try {
                deleteFile(objectName);
            } catch (Exception e) {
                log.error("Failed to delete file: {}", objectName, e);
            }
        }
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}

