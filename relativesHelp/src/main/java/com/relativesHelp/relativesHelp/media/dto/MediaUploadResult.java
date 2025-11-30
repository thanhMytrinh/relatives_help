package com.relativesHelp.relativesHelp.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadResult {
    private String secureUrl;
    private String publicId;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private Integer duration;
    private String resourceType;
}
