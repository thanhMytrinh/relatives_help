package com.relativesHelp.relativesHelp.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdatedEvent {
    private Long eventId;
    private Long familyTreeId;
    private Long updatedByUserId;
    private LocalDateTime updatedAt;
}


