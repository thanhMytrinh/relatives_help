package com.relativesHelp.relativesHelp.event.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_participants", indexes = {
    @Index(name = "idx_person", columnList = "person_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "unique_participant", 
        columnNames = {"event_id", "person_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Enumerated(EnumType.STRING)
    @Column(name = "rsvp_status", nullable = false)
    @Builder.Default
    private RsvpStatus rsvpStatus = RsvpStatus.PENDING;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    public enum RsvpStatus {
        PENDING, ACCEPTED, DECLINED, MAYBE
    }
}

