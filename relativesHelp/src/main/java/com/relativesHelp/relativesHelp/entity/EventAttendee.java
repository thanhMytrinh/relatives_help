package com.relativesHelp.relativesHelp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_attendees", indexes = @Index(name = "idx_event_status", columnList = "event_id, status"))
@Getter
@Setter
@NoArgsConstructor
public class EventAttendee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private FamilyEvent event;

    @ManyToOne(fetch = FetchType.LAZY)
    private FamilyMember member;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private AttendeeStatus status = AttendeeStatus.INVITED;

    @Column(name = "plus_ones")
    private int plusOnes = 0;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum AttendeeStatus { INVITED, ATTENDING, DECLINED, MAYBE }
}