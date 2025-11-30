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
public class MinIOPresignedUrlResponse {
    private String objectName;
    private String presignedUrl;
    private ZonedDateTime expirationTime;
    private int expirationSeconds;
    private String method; // GET, PUT, POST
}

