package com.relativesHelp.relativesHelp.search.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class PersonSearchResponse {
    Long id;
    Long familyTreeId;
    Long userId;
    String fullName;
    String gender;
    LocalDate dateOfBirth;
    LocalDate dateOfDeath;
    String placeOfBirth;
    String placeOfDeath;
    Boolean isAlive;
    String biography;
    String occupation;
    Integer generationLevel;
    String avatarUrl;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}


