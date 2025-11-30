package com.relativesHelp.relativesHelp.media.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MediaUploadRequest {
    @NotNull
    private Long familyTreeId;
    private Long personId;
    private String albumId;
    private String description;
    private List<String> tags;
    private Boolean isPublic;
}

