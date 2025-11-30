package com.relativesHelp.relativesHelp.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MinIOFileInfo {
    private String objectName;
    private long size;
    private String etag;
    private String contentType;
    private ZonedDateTime lastModified;
    private String url;
}

