package com.relativesHelp.relativesHelp.media.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MinIOCopyMoveRequest {
    @NotBlank(message = "Source object name is required")
    private String sourceObjectName;
    
    @NotBlank(message = "Destination object name is required")
    private String destObjectName;
}

