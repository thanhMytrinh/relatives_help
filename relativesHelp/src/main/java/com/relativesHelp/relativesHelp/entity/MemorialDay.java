package com.relativesHelp.relativesHelp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 14. MemorialDay (giỗ, sinh nhật, kỷ niệm cưới)
@Entity
@Table(name = "memorial_days", indexes = @Index(name = "idx_date", columnList = "memorial_date"))
@Getter
@Setter
@NoArgsConstructor
public class MemorialDay {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private FamilyMember member;

    @Column(name = "memorial_date", nullable = false)
    private LocalDate memorialDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "memorial_type")
    private MemorialType type = MemorialType.DEATH_ANNIVERSARY;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String location;

    @Column(name = "lunar_year_cycle")
    private Integer lunarYearCycle;

    @Column(name = "is_recurring")
    private boolean recurring = true;

    @Column(name = "notification_enabled")
    private boolean notificationEnabled = true;

    @Column(name = "notification_days_before")
    private int notificationDaysBefore = 3;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum MemorialType { DEATH_ANNIVERSARY, BIRTH_ANNIVERSARY, WEDDING_ANNIVERSARY }
}