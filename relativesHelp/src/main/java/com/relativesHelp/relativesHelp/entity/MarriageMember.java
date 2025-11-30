package com.relativesHelp.relativesHelp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "marriage_members", uniqueConstraints = {
        @UniqueConstraint(name = "uq_marriage_member", columnNames = {"marriage_id", "member_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class MarriageMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marriage_id", nullable = false)
    private Marriage marriage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private FamilyMember member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "order_index")
    private int orderIndex = 1;

    @Column(name = "is_current")
    private boolean current = true;

    public enum Role { HUSBAND, WIFE, PARTNER }
}