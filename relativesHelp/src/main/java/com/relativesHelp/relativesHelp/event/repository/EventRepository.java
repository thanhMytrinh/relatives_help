package com.relativesHelp.relativesHelp.event.repository;

import com.relativesHelp.relativesHelp.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByFamilyTreeId(Long familyTreeId);
    List<Event> findByPersonId(Long personId);
    
    @Query("SELECT e FROM Event e WHERE e.familyTreeId = :familyTreeId AND " +
           "e.eventDate >= :startDate AND e.eventDate <= :endDate " +
           "ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(@Param("familyTreeId") Long familyTreeId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
    
    @Query("SELECT e FROM Event e WHERE e.familyTreeId = :familyTreeId AND " +
           "e.eventDate = :date")
    List<Event> findByDate(@Param("familyTreeId") Long familyTreeId, 
                           @Param("date") LocalDate date);
}

