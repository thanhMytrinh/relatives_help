package com.relativesHelp.relativesHelp.media.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CloudinaryFolderDto {
    String name;
    String path;
}

