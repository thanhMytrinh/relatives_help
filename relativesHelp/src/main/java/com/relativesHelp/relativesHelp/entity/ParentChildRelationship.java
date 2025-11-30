package com.relativesHelp.relativesHelp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "parent_child_relationships", uniqueConstraints = {
        @UniqueConstraint(name = "uq_parent_child", columnNames = {"parent_id", "child_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class ParentChildRelationship {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private FamilyMember parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private FamilyMember child;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type")
    private RelationshipType relationshipType = RelationshipType.BIOLOGICAL;

    @Column(name = "is_primary")
    private boolean primary = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum RelationshipType { BIOLOGICAL, ADOPTED, STEPCHILD }
}