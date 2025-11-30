package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.AppointmentParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentParticipantRepository extends JpaRepository<AppointmentParticipant, Long> {
    List<AppointmentParticipant> findByAppointmentId(Long appointmentId);
    Optional<AppointmentParticipant> findByAppointmentIdAndUserId(Long appointmentId, Long userId);
    Optional<AppointmentParticipant> findByAppointmentIdAndMemberId(Long appointmentId, Long memberId);
}