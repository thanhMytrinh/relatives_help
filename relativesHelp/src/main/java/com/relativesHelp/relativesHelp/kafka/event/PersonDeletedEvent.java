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
public class PersonDeletedEvent {
    private Long personId;
    private Long familyTreeId;
    private Long deletedByUserId;
    private LocalDateTime deletedAt;
}


