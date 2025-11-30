package com.relativesHelp.relativesHelp.media.dto;

import com.relativesHelp.relativesHelp.media.document.MediaFile;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class MediaResponse {
    String id;
    Long familyTreeId;
    Long personId;
    Long uploadedByUserId;
    String fileName;
    String originalFileName;
    String fileType;
    Long fileSize;
    String cloudStorageUrl;
    String thumbnailUrl;
    String cloudinaryPublicId;
    String storageProvider;
    String resourceType;
    List<String> tags;
    String description;
    String albumId;
    Boolean isPublic;
    Integer viewCount;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static MediaResponse from(MediaFile file) {
        return MediaResponse.builder()
                .id(file.getId())
                .familyTreeId(file.getFamilyTreeId())
                .personId(file.getPersonId())
                .uploadedByUserId(file.getUploadedByUserId())
                .fileName(file.getFileName())
                .originalFileName(file.getOriginalFileName())
                .fileType(file.getFileType())
                .fileSize(file.getFileSize())
                .cloudStorageUrl(file.getCloudStorageUrl())
                .thumbnailUrl(file.getThumbnailUrl())
                .cloudinaryPublicId(file.getCloudinaryPublicId())
                .storageProvider(file.getStorageProvider())
                .resourceType(file.getResourceType())
                .tags(file.getTags())
                .description(file.getDescription())
                .albumId(file.getAlbumId())
                .isPublic(file.getIsPublic())
                .viewCount(file.getViewCount())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
    }
}

