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
public class RelationshipCreatedEvent {
    private Long relationshipId;
    private Long familyTreeId;
    private Long personId;
    private Long relatedPersonId;
    private String relationshipType;
    private Long createdByUserId;
    private LocalDateTime createdAt;
}

