package com.relativesHelp.relativesHelp.family.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateFamilyTreeRequest {
    @NotBlank(message = "Family tree name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    private String description;
}

