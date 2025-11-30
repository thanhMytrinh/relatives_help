package com.relativesHelp.relativesHelp.media.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CloudinaryResourceDto {
    String publicId;
    String assetId;
    String type;
    String resourceType;
    String format;
    String url;
    String secureUrl;
    Long bytes;
    Integer width;
    Integer height;
    String folder;
    String createdAt;
}

