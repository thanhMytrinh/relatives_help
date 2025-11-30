package com.relativesHelp.relativesHelp.media.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "media_files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFile {
    @Id
    private String id;

    @Indexed
    private Long familyTreeId;

    @Indexed
    private Long personId;

    @Indexed
    private Long uploadedByUserId;

    private String fileName;
    private String originalFileName;
    private String fileType; // image/jpeg, video/mp4, application/pdf
    private Long fileSize; // bytes

    private String cloudStorageUrl;
    private String cloudinaryPublicId;
    private String storageProvider;
    private String resourceType;
    private String thumbnailUrl;

    private MediaMetadata metadata;

    private List<String> tags;
    private String description;

    @Indexed
    private String albumId;

    @Indexed
    private Boolean isPublic;

    @Indexed
    private Integer viewCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaMetadata {
        private Integer width;
        private Integer height;
        private Integer duration; // For videos in seconds
        private LocalDateTime capturedDate;
        private Location location;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private String type = "Point";
        private List<Double> coordinates; // [longitude, latitude]
    }
}

