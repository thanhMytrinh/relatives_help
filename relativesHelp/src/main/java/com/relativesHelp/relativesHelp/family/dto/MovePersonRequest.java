package com.relativesHelp.relativesHelp.family.dto;

import com.relativesHelp.relativesHelp.family.entity.Relationship;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovePersonRequest {
    @NotNull(message = "New parent ID is required")
    private Long newParentId;

    private Relationship.RelationshipType relationshipType;

    // Position for visual layout
    private Double positionX;
    private Double positionY;
}

