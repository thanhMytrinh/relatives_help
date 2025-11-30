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
public class EventCreatedEvent {
    private Long eventId;
    private Long familyTreeId;
    private Long personId;
    private String eventType;
    private String title;
    private LocalDate eventDate;
    private Boolean isRecurring;
    private Integer reminderDays;
    private Long createdByUserId;
    private LocalDateTime createdAt;
}

