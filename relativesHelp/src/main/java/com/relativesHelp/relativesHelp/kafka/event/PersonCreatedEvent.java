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
public class PersonCreatedEvent {
    private Long personId;
    private Long familyTreeId;
    private String fullName;
    private Long createdByUserId;
    private LocalDateTime createdAt;
}

