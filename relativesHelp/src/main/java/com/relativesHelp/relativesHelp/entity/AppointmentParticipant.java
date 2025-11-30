package com.relativesHelp.relativesHelp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_participants")
@Getter
@Setter
@NoArgsConstructor
public class AppointmentParticipant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY) private User user;
    @ManyToOne(fetch = FetchType.LAZY) private FamilyMember member;
    private String email;
    private String phone;

    @Enumerated(EnumType.STRING)
    private ParticipantRole participantRole = ParticipantRole.REQUIRED;

    @Enumerated(EnumType.STRING)
    private RsvpStatus rsvpStatus = RsvpStatus.PENDING;

    private LocalDateTime respondedAt;
    private boolean checkedIn = false;

    public enum ParticipantRole { ORGANIZER, CO_ORGANIZER, REQUIRED, OPTIONAL }
    public enum RsvpStatus { PENDING, ACCEPTED, DECLINED, TENTATIVE, NO_RESPONSE }
}