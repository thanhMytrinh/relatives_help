package com.relativesHelp.relativesHelp.family.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePersonPositionRequest {
    @NotNull(message = "Position X is required")
    private Double positionX;

    @NotNull(message = "Position Y is required")
    private Double positionY;

    private Integer generationLevel; // Optional: update generation if moving
}

