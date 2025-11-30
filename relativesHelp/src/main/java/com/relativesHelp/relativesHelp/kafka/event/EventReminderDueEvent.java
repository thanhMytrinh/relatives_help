package com.relativesHelp.relativesHelp.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventReminderDueEvent {
    private Long eventId;
    private Long notificationScheduleId;
    private Long userId;
    private String eventTitle;
    private LocalDate eventDate;
    private String notificationType;
    private LocalDateTime reminderTime;
}

