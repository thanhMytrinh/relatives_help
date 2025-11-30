package com.relativesHelp.relativesHelp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 16. FamilyEvent (đám giỗ, họp mặt, cưới hỏi…)
@Entity
@Table(name = "family_events")
@Getter
@Setter
@NoArgsConstructor
public class FamilyEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "event_date_lunar")
    private LocalDate eventDateLunar;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @Column(name = "is_recurring")
    private boolean recurring = false;

    @Column(name = "max_attendees")
    private Integer maxAttendees;

    @Column(name = "is_public")
    private boolean publicEvent = false;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum EventType { REUNION, BIRTHDAY, WEDDING, DEATH_ANNIVERSARY, ACHIEVEMENT, CUSTOM }
}