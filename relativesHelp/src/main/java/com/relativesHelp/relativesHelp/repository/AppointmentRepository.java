package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findByFamilyId(Long familyId, Pageable pageable);
    List<Appointment> findByFamilyIdAndStartDatetimeBetween(Long familyId, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByCreatedById(Long userId);
}