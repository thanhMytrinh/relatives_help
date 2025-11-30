package com.relativesHelp.relativesHelp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments", indexes = @Index(name = "idx_datetime", columnList = "start_datetime"))
@Getter
@Setter
@NoArgsConstructor
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false)
    private AppointmentType type;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;
    private Double locationLat;
    private Double locationLng;
    private String meetingUrl;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    private String timezone = "Asia/Ho_Chi_Minh";
    private boolean allDay = false;
    private boolean recurring = false;
    private String recurrenceRule;
    private LocalDate recurrenceEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_appointment_id")
    private Appointment parentAppointment;

    @Column(name = "reminder_enabled")
    private boolean reminderEnabled = true;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    private String color = "#3B82F6";

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "is_private")
    private boolean privateAppointment = false;

    @Column(name = "ical_uid", unique = true)
    private String icalUid;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum AppointmentType { FAMILY_MEETING, GENEALOGY_RESEARCH, PHOTO_SESSION, ANCESTOR_WORSHIP, REUNION, CELEBRATION, MEDICAL, LEGAL, OTHER }
    public enum AppointmentStatus { SCHEDULED, CONFIRMED, CANCELLED, COMPLETED }
    public enum Priority { LOW, MEDIUM, HIGH, URGENT }
}