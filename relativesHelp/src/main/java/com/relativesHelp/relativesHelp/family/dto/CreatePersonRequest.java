package com.relativesHelp.relativesHelp.family.dto;

import com.relativesHelp.relativesHelp.family.entity.Person;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePersonRequest {
    private Long userId;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotNull(message = "Gender is required")
    private Person.Gender gender;

    private String dateOfBirth;
    private String dateOfDeath;
    private String placeOfBirth;
    private String placeOfDeath;
    private Boolean isAlive;
    private String biography;
    private String avatarUrl;
    private String occupation;
    private Integer generationLevel;
    private Double positionX;
    private Double positionY;
}

