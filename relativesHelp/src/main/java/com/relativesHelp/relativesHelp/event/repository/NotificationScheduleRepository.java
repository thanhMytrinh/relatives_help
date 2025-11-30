package com.relativesHelp.relativesHelp.event.repository;

import com.relativesHelp.relativesHelp.event.entity.NotificationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationScheduleRepository extends JpaRepository<NotificationSchedule, Long> {
    List<NotificationSchedule> findByEventId(Long eventId);
    List<NotificationSchedule> findByUserId(Long userId);
    
    @Query("SELECT n FROM NotificationSchedule n WHERE n.notificationDate <= :now " +
           "AND n.isSent = false ORDER BY n.notificationDate ASC")
    List<NotificationSchedule> findPendingNotifications(@Param("now") LocalDateTime now);
}

