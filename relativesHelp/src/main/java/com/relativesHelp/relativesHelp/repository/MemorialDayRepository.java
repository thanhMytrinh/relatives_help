package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.MemorialDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MemorialDayRepository extends JpaRepository<MemorialDay, Long> {
    List<MemorialDay> findByFamilyId(Long familyId);

    @Query("SELECT m FROM MemorialDay m WHERE m.memorialDate = :date AND m.notificationEnabled = true")
    List<MemorialDay> findTodayMemorials(@Param("date") LocalDate date);

    @Query("SELECT m FROM MemorialDay m WHERE m.memorialDate BETWEEN :start AND :end AND m.notificationEnabled = true")
    List<MemorialDay> findUpcomingMemorials(@Param("start") LocalDate start, @Param("end") LocalDate end);
}