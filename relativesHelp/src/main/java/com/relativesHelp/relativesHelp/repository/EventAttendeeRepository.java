package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.EventAttendee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventAttendeeRepository extends JpaRepository<EventAttendee, Long> {
    List<EventAttendee> findByEventId(Long eventId);
    Optional<EventAttendee> findByEventIdAndUserId(Long eventId, Long userId);
    Optional<EventAttendee> findByEventIdAndMemberId(Long eventId, Long memberId);
}
