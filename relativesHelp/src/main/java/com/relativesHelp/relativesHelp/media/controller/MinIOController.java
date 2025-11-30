package com.relativesHelp.relativesHelp.media.controller;

import com.relativesHelp.relativesHelp.common.dto.ApiResponse;
import com.relativesHelp.relativesHelp.media.dto.MinIOFileInfo;
import com.relativesHelp.relativesHelp.media.dto.MinIOFileResponse;
import com.relativesHelp.relativesHelp.media.dto.MinIOFolderInfo;
import com.relativesHelp.relativesHelp.media.dto.MinIOPresignedUrlResponse;
import com.relativesHelp.relativesHelp.media.dto.MinIOCopyMoveRequest;
import com.relativesHelp.relativesHelp.media.service.MinIOFolderService;
import com.relativesHelp.relativesHelp.media.service.MinIOService;
import io.minio.http.Method;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/minio")
@RequiredArgsConstructor
public class MinIOController {

    private final MinIOService minIOService;
    private final MinIOFolderService minIOFolderService;

    /**
     * Upload file to MinIO
     * POST /api/v1/minio/upload
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MinIOFileResponse>> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String folderPath) {
        
        String url = minIOService.uploadFile(file, folderPath);
        String objectName = minIOService.extractObjectNameFromUrl(url);
        
        MinIOFileResponse response = MinIOFileResponse.builder()
                .url(url)
                .objectName(objectName)
                .folderPath(folderPath)
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", response));
    }

    /**
     * Upload file with specific ID
     * POST /api/v1/minio/upload/{fileId}
     */
    @PostMapping(value = "/upload/{fileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MinIOFileResponse>> uploadFileById(
            @PathVariable String fileId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String folderPath) {
        
        String url = minIOService.uploadFileById(file, fileId, folderPath);
        
        MinIOFileResponse response = MinIOFileResponse.builder()
                .fileId(fileId)
                .url(url)
                .objectName(minIOService.extractObjectNameFromUrl(url))
                .folderPath(folderPath)
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully with ID", response));
    }

    /**
     * Download file by ID
     * GET /api/v1/minio/files/{fileId}
     */
    @GetMapping("/files/{fileId}")
    public ResponseEntity<InputStreamResource> downloadFileById(
            @PathVariable String fileId,
            @RequestParam(required = false) String folderPath) {
        
        InputStream inputStream = minIOService.getFileById(fileId, folderPath);
        InputStreamResource resource = new InputStreamResource(inputStream);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileId + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     * Get file info by ID
     * GET /api/v1/minio/files/{fileId}/info
     */
    @GetMapping("/files/{fileId}/info")
    public ResponseEntity<ApiResponse<MinIOFileInfo>> getFileInfoById(
            @PathVariable String fileId,
            @RequestParam(required = false) String folderPath) {
        
        MinIOFileInfo fileInfo = minIOService.getFileInfoById(fileId, folderPath);
        return ResponseEntity.ok(ApiResponse.success(fileInfo));
    }

    /**
     * Get file URL by ID
     * GET /api/v1/minio/files/{fileId}/url
     */
    @GetMapping("/files/{fileId}/url")
    public ResponseEntity<ApiResponse<Map<String, String>>> getFileUrlById(
            @PathVariable String fileId,
            @RequestParam(required = false) String folderPath) {
        
        String url = minIOService.getFileUrlById(fileId, folderPath);
        Map<String, String> response = new HashMap<>();
        response.put("fileId", fileId);
        response.put("url", url);
        response.put("folderPath", folderPath != null ? folderPath : "");
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Check if file exists by ID
     * GET /api/v1/minio/files/{fileId}/exists
     */
    @GetMapping("/files/{fileId}/exists")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkFileExists(
            @PathVariable String fileId,
            @RequestParam(required = false) String folderPath) {
        
        boolean exists = minIOService.fileExistsById(fileId, folderPath);
        Map<String, Object> response = new HashMap<>();
        response.put("fileId", fileId);
        response.put("exists", exists);
        response.put("folderPath", folderPath != null ? folderPath : "");
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Delete file by ID
     * DELETE /api/v1/minio/files/{fileId}
     */
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<ApiResponse<Void>> deleteFileById(
            @PathVariable String fileId,
            @RequestParam(required = false) String folderPath) {
        
        minIOService.deleteFileById(fileId, folderPath);
        return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
    }

    /**
     * List files in a folder
     * GET /api/v1/minio/files
     */
    @GetMapping("/files")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listFiles(
            @RequestParam(required = false) String folderPath,
            @RequestParam(defaultValue = "false") boolean recursive) {
        
        List<String> files = recursive
                ? minIOService.listFilesRecursive(folderPath)
                : minIOService.listFiles(folderPath);
        
        Map<String, Object> response = new HashMap<>();
        response.put("files", files);
        response.put("folderPath", folderPath != null ? folderPath : "");
        response.put("count", files.size());
        response.put("recursive", recursive);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Copy file
     * POST /api/v1/minio/copy
     */
    @PostMapping("/copy")
    public ResponseEntity<ApiResponse<Void>> copyFile(
            @Valid @RequestBody MinIOCopyMoveRequest request) {
        
        minIOService.copyFile(request.getSourceObjectName(), request.getDestObjectName());
        return ResponseEntity.ok(ApiResponse.success("File copied successfully", null));
    }

    /**
     * Move file
     * POST /api/v1/minio/move
     */
    @PostMapping("/move")
    public ResponseEntity<ApiResponse<Void>> moveFile(
            @Valid @RequestBody MinIOCopyMoveRequest request) {
        
        minIOService.moveFile(request.getSourceObjectName(), request.getDestObjectName());
        return ResponseEntity.ok(ApiResponse.success("File moved successfully", null));
    }

    /**
     * Create folder
     * POST /api/v1/minio/folders
     */
    @PostMapping("/folders")
    public ResponseEntity<ApiResponse<Map<String, String>>> createFolder(
            @RequestParam String folderPath) {
        
        minIOService.createFolder(folderPath);
        Map<String, String> response = new HashMap<>();
        response.put("folderPath", folderPath);
        response.put("message", "Folder created successfully");
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get folders for a user
     * GET /api/v1/minio/folders/user/{userId}
     */
    @GetMapping("/folders/user/{userId}")
    public ResponseEntity<ApiResponse<List<MinIOFolderInfo>>> getUserFolders(
            @PathVariable Long userId) {
        
        List<MinIOFolderInfo> folders = minIOFolderService.getUserFolders(userId);
        return ResponseEntity.ok(ApiResponse.success(folders));
    }

    /**
     * Get folders for a family
     * GET /api/v1/minio/folders/family/{familyId}
     */
    @GetMapping("/folders/family/{familyId}")
    public ResponseEntity<ApiResponse<List<MinIOFolderInfo>>> getFamilyFolders(
            @PathVariable Long familyId) {
        
        List<MinIOFolderInfo> folders = minIOFolderService.getFamilyFolders(familyId);
        return ResponseEntity.ok(ApiResponse.success(folders));
    }

    /**
     * Get folders for a person
     * GET /api/v1/minio/folders/person/{personId}
     */
    @GetMapping("/folders/person/{personId}")
    public ResponseEntity<ApiResponse<List<MinIOFolderInfo>>> getPersonFolders(
            @PathVariable Long personId,
            @RequestParam Long familyId) {
        
        List<MinIOFolderInfo> folders = minIOFolderService.getPersonFolders(personId, familyId);
        return ResponseEntity.ok(ApiResponse.success(folders));
    }

    /**
     * Get folders for an album
     * GET /api/v1/minio/folders/album/{albumId}
     */
    @GetMapping("/folders/album/{albumId}")
    public ResponseEntity<ApiResponse<List<MinIOFolderInfo>>> getAlbumFolders(
            @PathVariable String albumId,
            @RequestParam Long familyId) {
        
        List<MinIOFolderInfo> folders = minIOFolderService.getAlbumFolders(albumId, familyId);
        return ResponseEntity.ok(ApiResponse.success(folders));
    }

    /**
     * Get all folders for entities related to media
     * GET /api/v1/minio/folders/entities
     */
    @GetMapping("/folders/entities")
    public ResponseEntity<ApiResponse<List<MinIOFolderInfo>>> getAllEntityFolders() {
        
        List<MinIOFolderInfo> folders = minIOFolderService.getAllEntityFolders();
        return ResponseEntity.ok(ApiResponse.success(folders));
    }

    /**
     * Generate presigned URL for file access
     * GET /api/v1/minio/files/{fileId}/presigned-url
     */
    @GetMapping("/files/{fileId}/presigned-url")
    public ResponseEntity<ApiResponse<MinIOPresignedUrlResponse>> generatePresignedUrl(
            @PathVariable String fileId,
            @RequestParam(required = false) String folderPath,
            @RequestParam(defaultValue = "3600") int expirationSeconds,
            @RequestParam(defaultValue = "GET") String method) {
        
        String objectName = folderPath != null && !folderPath.isEmpty()
                ? folderPath + "/" + fileId
                : fileId;
        
        Method httpMethod = method.equalsIgnoreCase("PUT") ? Method.PUT : Method.GET;
        MinIOPresignedUrlResponse response = minIOService.generatePresignedUrl(objectName, expirationSeconds, httpMethod);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Generate presigned upload URL
     * GET /api/v1/minio/files/{fileId}/presigned-upload-url
     */
    @GetMapping("/files/{fileId}/presigned-upload-url")
    public ResponseEntity<ApiResponse<MinIOPresignedUrlResponse>> generatePresignedUploadUrl(
            @PathVariable String fileId,
            @RequestParam(required = false) String folderPath,
            @RequestParam(defaultValue = "3600") int expirationSeconds) {
        
        String objectName = folderPath != null && !folderPath.isEmpty()
                ? folderPath + "/" + fileId
                : fileId;
        
        MinIOPresignedUrlResponse response = minIOService.generatePresignedUploadUrl(objectName);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Validate file before upload
     * POST /api/v1/minio/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "50") int maxSizeMB) {
        
        Map<String, Object> validation = new HashMap<>();
        boolean isValid = true;
        List<String> errors = new ArrayList<>();
        
        // Validate file size
        if (!minIOService.validateFileSize(file.getSize(), maxSizeMB)) {
            isValid = false;
            errors.add("File size exceeds maximum allowed size: " + maxSizeMB + "MB");
        }
        
        // Validate content type for images
        if (minIOService.isImage(file.getContentType())) {
            if (!minIOService.validateImageContentType(file.getContentType())) {
                isValid = false;
                errors.add("Invalid image format. Allowed: JPEG, PNG, GIF, WebP, BMP, SVG");
            }
        }
        
        // Validate content type for videos
        if (minIOService.isVideo(file.getContentType())) {
            if (!minIOService.validateVideoContentType(file.getContentType())) {
                isValid = false;
                errors.add("Invalid video format. Allowed: MP4, MPEG, QuickTime, AVI, WebM, MKV, 3GPP");
            }
        }
        
        validation.put("valid", isValid);
        validation.put("fileName", file.getOriginalFilename());
        validation.put("fileSize", file.getSize());
        validation.put("contentType", file.getContentType());
        validation.put("isImage", minIOService.isImage(file.getContentType()));
        validation.put("isVideo", minIOService.isVideo(file.getContentType()));
        validation.put("errors", errors);
        
        return ResponseEntity.ok(ApiResponse.success(validation));
    }

    /**
     * Set object metadata
     * PUT /api/v1/minio/files/{fileId}/metadata
     */
    @PutMapping("/files/{fileId}/metadata")
    public ResponseEntity<ApiResponse<Void>> setObjectMetadata(
            @PathVariable String fileId,
            @RequestParam(required = false) String folderPath,
            @RequestBody Map<String, String> metadata) {
        
        String objectName = folderPath != null && !folderPath.isEmpty()
                ? folderPath + "/" + fileId
                : fileId;
        
        minIOService.setObjectMetadata(objectName, metadata);
        return ResponseEntity.ok(ApiResponse.success("Metadata updated successfully", null));
    }

    /**
     * Get object metadata
     * GET /api/v1/minio/files/{fileId}/metadata
     */
    @GetMapping("/files/{fileId}/metadata")
    public ResponseEntity<ApiResponse<Map<String, String>>> getObjectMetadata(
            @PathVariable String fileId,
            @RequestParam(required = false) String folderPath) {
        
        String objectName = folderPath != null && !folderPath.isEmpty()
                ? folderPath + "/" + fileId
                : fileId;
        
        Map<String, String> metadata = minIOService.getObjectMetadata(objectName);
        return ResponseEntity.ok(ApiResponse.success(metadata));
    }

    /**
     * Batch upload files
     * POST /api/v1/minio/batch/upload
     */
    @PostMapping(value = "/batch/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpload(
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam(required = false) String folderPath) {
        
        List<String> urls = minIOService.batchUploadFiles(files, folderPath);
        Map<String, Object> response = new HashMap<>();
        response.put("uploaded", urls.size());
        response.put("total", files.size());
        response.put("urls", urls);
        
        return ResponseEntity.ok(ApiResponse.success("Batch upload completed", response));
    }

    /**
     * Batch delete files
     * DELETE /api/v1/minio/batch/delete
     */
    @DeleteMapping("/batch/delete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDelete(
            @RequestBody List<String> objectNames) {
        
        minIOService.batchDeleteFiles(objectNames);
        Map<String, Object> response = new HashMap<>();
        response.put("deleted", objectNames.size());
        
        return ResponseEntity.ok(ApiResponse.success("Batch delete completed", response));
    }
}

