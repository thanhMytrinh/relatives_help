package com.relativesHelp.relativesHelp.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MinIOVideoMetadata {
    private Integer width;
    private Integer height;
    private Long duration; // in seconds
    private String format; // MP4, AVI, MOV, etc.
    private String codec;
    private Long fileSize;
    private Double frameRate;
    private Long bitrate;
}

