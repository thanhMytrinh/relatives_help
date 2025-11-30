package com.relativesHelp.relativesHelp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "family_members", indexes = {
        @Index(name = "idx_family_gen", columnList = "family_id, generation"),
        @Index(name = "idx_alive", columnList = "family_id, is_alive")
})
@Getter
@Setter
@NoArgsConstructor
public class FamilyMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "birth_date_lunar")
    private LocalDate birthDateLunar;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @Column(name = "death_date_lunar")
    private LocalDate deathDateLunar;

    @Column(name = "is_alive")
    private boolean alive = true;

    @Column(nullable = false)
    private int generation;

    @Column(name = "display_order")
    private int displayOrder = 0;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    private String occupation;
    private String location;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Gender { MALE, FEMALE, OTHER }
}