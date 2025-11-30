package com.relativesHelp.relativesHelp.media.dto;

import lombok.Data;

import java.util.List;

@Data
public class MediaUpdateRequest {
    private String description;
    private List<String> tags;
    private String albumId;
    private Boolean isPublic;
}

