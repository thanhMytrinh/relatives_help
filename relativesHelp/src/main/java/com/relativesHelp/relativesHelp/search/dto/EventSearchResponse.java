package com.relativesHelp.relativesHelp.search.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class EventSearchResponse {
    Long id;
    Long familyTreeId;
    Long personId;
    Long eventTypeId;
    String title;
    String description;
    LocalDate eventDate;
    String eventTime;
    Boolean isRecurring;
    String recurrenceRule;
    String location;
    Boolean isLunarCalendar;
    Integer reminderDays;
    Long createdByUserId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}


