package com.relativesHelp.relativesHelp.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadedEvent {
    private String mediaId;
    private Long familyTreeId;
    private Long personId;
    private Long uploadedByUserId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String cloudStorageUrl;
    private LocalDateTime uploadedAt;
}

