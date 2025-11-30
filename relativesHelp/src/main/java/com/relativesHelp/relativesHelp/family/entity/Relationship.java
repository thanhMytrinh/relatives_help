package com.relativesHelp.relativesHelp.family.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "relationships", indexes = {
    @Index(name = "idx_person", columnList = "person_id"),
    @Index(name = "idx_related_person", columnList = "related_person_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "unique_relationship", 
        columnNames = {"person_id", "related_person_id", "relationship_type"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_tree_id", nullable = false)
    private Long familyTreeId;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Column(name = "related_person_id", nullable = false)
    private Long relatedPersonId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private RelationshipType relationshipType;

    @Column(name = "marriage_date")
    private LocalDate marriageDate;

    @Column(name = "divorce_date")
    private LocalDate divorceDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum RelationshipType {
        FATHER, MOTHER, SON, DAUGHTER,
        SPOUSE, SIBLING,
        GRANDFATHER, GRANDMOTHER,
        GRANDSON, GRANDDAUGHTER,
        UNCLE, AUNT, NEPHEW, NIECE
    }
}

