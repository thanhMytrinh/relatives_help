package com.relativesHelp.relativesHelp.family.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyTreeStructureResponse {
    private Long id;
    private String name;
    private String fullName;
    private String avatarUrl;
    private Gender gender;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private Boolean isAlive;
    private Integer generationLevel;
    private Double positionX; // For drag & drop positioning
    private Double positionY; // For drag & drop positioning
    private List<FamilyTreeStructureResponse> children;
    private List<FamilyTreeStructureResponse> spouses;
    private List<Long> parentIds; // IDs of parents

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}

