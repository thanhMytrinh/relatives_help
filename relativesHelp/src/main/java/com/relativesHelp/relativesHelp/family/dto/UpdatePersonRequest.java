package com.relativesHelp.relativesHelp.family.dto;

import com.relativesHelp.relativesHelp.family.entity.Person;
import lombok.Data;

@Data
public class UpdatePersonRequest {
    private String fullName;
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

