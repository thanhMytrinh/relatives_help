package com.relativesHelp.relativesHelp.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MinIOFileResponse {
    private String fileId;
    private String url;
    private String objectName;
    private String folderPath;
}

