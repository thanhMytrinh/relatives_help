package com.relativesHelp.relativesHelp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 18. WorshipSchedule (lịch cúng, lễ)
@Entity
@Table(name = "worship_schedules")
@Getter
@Setter
@NoArgsConstructor
public class WorshipSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private FamilyMember member; // người được cúng

    @Enumerated(EnumType.STRING)
    @Column(name = "worship_type", nullable = false)
    private WorshipType worshipType;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_member_id")
    private FamilyMember responsibleMember;

    @Column(name = "is_completed")
    private boolean completed = false;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum WorshipType { DAILY, MONTHLY, YEARLY, SPECIAL }
}