package com.relativesHelp.relativesHelp.family.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "persons", indexes = {
    @Index(name = "idx_family_tree", columnList = "family_tree_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_generation", columnList = "generation_level")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_tree_id", nullable = false)
    private Long familyTreeId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "date_of_death")
    private LocalDate dateOfDeath;

    @Column(name = "place_of_birth", length = 200)
    private String placeOfBirth;

    @Column(name = "place_of_death", length = 200)
    private String placeOfDeath;

    @Column(name = "is_alive", nullable = false)
    @Builder.Default
    private Boolean isAlive = true;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(length = 100)
    private String occupation;

    @Column(name = "generation_level", nullable = false)
    @Builder.Default
    private Integer generationLevel = 0;

    @Column(name = "position_x")
    private Double positionX; // For drag & drop positioning

    @Column(name = "position_y")
    private Double positionY; // For drag & drop positioning

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}

