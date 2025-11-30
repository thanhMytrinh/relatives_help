package com.relativesHelp.relativesHelp.media.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MinIOFolderInfo {
    private String folderPath;
    private String folderName;
    private int fileCount;
    private List<String> subFolders;
    private List<String> files;
    private String entityType; // user, family, person, album
    private Long entityId;
}

