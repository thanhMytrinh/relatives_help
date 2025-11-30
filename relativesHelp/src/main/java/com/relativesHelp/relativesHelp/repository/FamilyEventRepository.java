package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.FamilyEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FamilyEventRepository extends JpaRepository<FamilyEvent, Long> {
    List<FamilyEvent> findByFamilyId(Long familyId);
    List<FamilyEvent> findByEventDateBetween(LocalDate start, LocalDate end);
}