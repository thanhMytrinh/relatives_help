package com.relativesHelp.relativesHelp.event.repository;

import com.relativesHelp.relativesHelp.event.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    List<EventParticipant> findByEventId(Long eventId);
    List<EventParticipant> findByPersonId(Long personId);
    Optional<EventParticipant> findByEventIdAndPersonId(Long eventId, Long personId);
}

