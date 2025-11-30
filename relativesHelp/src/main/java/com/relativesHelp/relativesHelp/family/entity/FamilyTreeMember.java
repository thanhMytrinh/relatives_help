package com.relativesHelp.relativesHelp.family.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "family_tree_members", indexes = {
    @Index(name = "idx_user", columnList = "user_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "unique_member", 
        columnNames = {"family_tree_id", "user_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyTreeMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_tree_id", nullable = false)
    private Long familyTreeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MemberRole role = MemberRole.VIEWER;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    public enum MemberRole {
        OWNER, ADMIN, EDITOR, VIEWER
    }
}

