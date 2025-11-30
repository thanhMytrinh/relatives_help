package com.relativesHelp.relativesHelp.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MinIOImageMetadata {
    private Integer width;
    private Integer height;
    private String format; // JPEG, PNG, GIF, etc.
    private Long fileSize;
    private String colorSpace;
    private Integer bitDepth;
}

