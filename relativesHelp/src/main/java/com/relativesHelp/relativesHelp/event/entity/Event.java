package com.relativesHelp.relativesHelp.event.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_family_tree", columnList = "family_tree_id"),
    @Index(name = "idx_person", columnList = "person_id"),
    @Index(name = "idx_event_date", columnList = "event_date"),
    @Index(name = "idx_created_by", columnList = "created_by_user_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_tree_id")
    private Long familyTreeId;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "event_type_id", nullable = false)
    private Long eventTypeId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "event_time")
    private LocalTime eventTime;

    @Column(name = "is_recurring", nullable = false)
    @Builder.Default
    private Boolean isRecurring = false;

    @Column(name = "recurrence_rule", length = 255)
    private String recurrenceRule;

    @Column(length = 255)
    private String location;

    @Column(name = "is_lunar_calendar", nullable = false)
    @Builder.Default
    private Boolean isLunarCalendar = false;

    @Column(name = "reminder_days", nullable = false)
    @Builder.Default
    private Integer reminderDays = 7;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

