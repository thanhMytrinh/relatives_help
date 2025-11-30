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
public class DragDropRelationshipRequest {
    @NotNull(message = "Person ID is required")
    private Long personId;

    @NotNull(message = "Target person ID is required")
    private Long targetPersonId;

    @NotNull(message = "Relationship type is required")
    private Relationship.RelationshipType relationshipType;

    // Position for visual layout
    private Double positionX;
    private Double positionY;

    // Action type: CREATE, UPDATE, DELETE
    private ActionType action;

    public enum ActionType {
        CREATE,    // Create new relationship
        UPDATE,    // Update existing relationship
        DELETE,    // Delete relationship
        MOVE       // Move person to new parent
    }
}

