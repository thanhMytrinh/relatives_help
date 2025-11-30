package com.relativesHelp.relativesHelp.family.controller;

import com.relativesHelp.relativesHelp.common.dto.ApiResponse;
import com.relativesHelp.relativesHelp.family.dto.CreateFamilyTreeRequest;
import com.relativesHelp.relativesHelp.family.dto.CreatePersonRequest;
import com.relativesHelp.relativesHelp.family.dto.DragDropRelationshipRequest;
import com.relativesHelp.relativesHelp.family.dto.FamilyTreeStructureResponse;
import com.relativesHelp.relativesHelp.family.dto.MovePersonRequest;
import com.relativesHelp.relativesHelp.family.dto.UpdatePersonPositionRequest;
import com.relativesHelp.relativesHelp.family.dto.UpdatePersonRequest;
import com.relativesHelp.relativesHelp.family.entity.FamilyTree;
import com.relativesHelp.relativesHelp.family.entity.Person;
import com.relativesHelp.relativesHelp.family.entity.Relationship;
import com.relativesHelp.relativesHelp.family.repository.PersonRepository;
import com.relativesHelp.relativesHelp.family.service.FamilyTreeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/family-trees")
@RequiredArgsConstructor
public class FamilyTreeController {

    private final FamilyTreeService familyTreeService;
    private final PersonRepository personRepository;

    /**
     * Get all family trees
     * GET /api/v1/family-trees
     */
    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<FamilyTree>>> getAllFamilyTrees() {
        java.util.List<FamilyTree> trees = familyTreeService.getAllFamilyTrees();
        return ResponseEntity.ok(ApiResponse.success(trees));
    }

    /**
     * Create a new family tree
     * POST /api/v1/family-trees
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FamilyTree>> createFamilyTree(
            @Valid @RequestBody CreateFamilyTreeRequest request,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        FamilyTree tree = familyTreeService.createFamilyTree(
                request.getName(),
                request.getDescription(),
                userId
        );
        return ResponseEntity.ok(ApiResponse.success("Family tree created successfully", tree));
    }

    /**
     * Create a new person in a family tree
     * POST /api/v1/family-trees/{treeId}/persons
     */
    @PostMapping("/{treeId}/persons")
    public ResponseEntity<ApiResponse<Person>> createPerson(
            @PathVariable Long treeId,
            @Valid @RequestBody CreatePersonRequest request,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        
        Person person = Person.builder()
                .familyTreeId(treeId)
                .userId(request.getUserId())
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth() != null ? 
                    java.time.LocalDate.parse(request.getDateOfBirth()) : null)
                .dateOfDeath(request.getDateOfDeath() != null ? 
                    java.time.LocalDate.parse(request.getDateOfDeath()) : null)
                .placeOfBirth(request.getPlaceOfBirth())
                .placeOfDeath(request.getPlaceOfDeath())
                .isAlive(request.getIsAlive() != null ? request.getIsAlive() : true)
                .biography(request.getBiography())
                .avatarUrl(request.getAvatarUrl())
                .occupation(request.getOccupation())
                .generationLevel(request.getGenerationLevel() != null ? request.getGenerationLevel() : 0)
                .positionX(request.getPositionX())
                .positionY(request.getPositionY())
                .build();
        
        Person savedPerson = familyTreeService.createPerson(person, userId);
        return ResponseEntity.ok(ApiResponse.success("Person created successfully", savedPerson));
    }

    /**
     * Get family tree structure for drag & drop visualization
     * GET /api/v1/family-trees/{treeId}/structure
     */
    @GetMapping("/{treeId}/structure")
    public ResponseEntity<ApiResponse<FamilyTreeStructureResponse>> getFamilyTreeStructure(
            @PathVariable Long treeId) {
        
        FamilyTreeStructureResponse structure = familyTreeService.getFamilyTreeStructure(treeId);
        return ResponseEntity.ok(ApiResponse.success(structure));
    }

    /**
     * Update relationship via drag & drop
     * PUT /api/v1/family-trees/{treeId}/relationships
     */
    @PutMapping("/{treeId}/relationships")
    public ResponseEntity<ApiResponse<Relationship>> updateRelationshipByDragDrop(
            @PathVariable Long treeId,
            @Valid @RequestBody DragDropRelationshipRequest request,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Relationship relationship = familyTreeService.updateRelationshipByDragDrop(
                treeId, request, userId);
        
        return ResponseEntity.ok(ApiResponse.success("Relationship updated successfully", relationship));
    }

    /**
     * Move person to new parent (change parent-child relationship)
     * PUT /api/v1/family-trees/{treeId}/persons/{personId}/parent
     */
    @PutMapping("/{treeId}/persons/{personId}/parent")
    public ResponseEntity<ApiResponse<Person>> movePersonToParent(
            @PathVariable Long treeId,
            @PathVariable Long personId,
            @Valid @RequestBody MovePersonRequest request,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Person person = familyTreeService.movePersonToParent(treeId, personId, request, userId);
        
        return ResponseEntity.ok(ApiResponse.success("Person moved successfully", person));
    }

    /**
     * Update person position (for drag & drop)
     * PUT /api/v1/family-trees/{treeId}/persons/{personId}/position
     */
    @PutMapping("/{treeId}/persons/{personId}/position")
    public ResponseEntity<ApiResponse<Person>> updatePersonPosition(
            @PathVariable Long treeId,
            @PathVariable Long personId,
            @Valid @RequestBody UpdatePersonPositionRequest request,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        Person person = familyTreeService.updatePersonPosition(treeId, personId, request, userId);
        
        return ResponseEntity.ok(ApiResponse.success("Position updated successfully", person));
    }

    /**
     * Get all persons in a family tree
     * GET /api/v1/family-trees/{treeId}/persons
     */
    @GetMapping("/{treeId}/persons")
    public ResponseEntity<ApiResponse<java.util.List<Person>>> getAllPersonsInFamilyTree(
            @PathVariable Long treeId) {
        
        java.util.List<Person> persons = personRepository.findByFamilyTreeId(treeId);
        return ResponseEntity.ok(ApiResponse.success(persons));
    }

    /**
     * Update person information
     * PUT /api/v1/family-trees/{treeId}/persons/{personId}
     */
    @PutMapping("/{treeId}/persons/{personId}")
    public ResponseEntity<ApiResponse<Person>> updatePerson(
            @PathVariable Long treeId,
            @PathVariable Long personId,
            @Valid @RequestBody UpdatePersonRequest request,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));
        if (!person.getFamilyTreeId().equals(treeId)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Person does not belong to this family tree"));
        }
        
        // Update fields if provided
        if (request.getFullName() != null) {
            person.setFullName(request.getFullName());
        }
        if (request.getGender() != null) {
            person.setGender(request.getGender());
        }
        if (request.getDateOfBirth() != null) {
            person.setDateOfBirth(java.time.LocalDate.parse(request.getDateOfBirth()));
        }
        if (request.getDateOfDeath() != null) {
            person.setDateOfDeath(java.time.LocalDate.parse(request.getDateOfDeath()));
        }
        if (request.getPlaceOfBirth() != null) {
            person.setPlaceOfBirth(request.getPlaceOfBirth());
        }
        if (request.getPlaceOfDeath() != null) {
            person.setPlaceOfDeath(request.getPlaceOfDeath());
        }
        if (request.getIsAlive() != null) {
            person.setIsAlive(request.getIsAlive());
        }
        if (request.getBiography() != null) {
            person.setBiography(request.getBiography());
        }
        if (request.getAvatarUrl() != null) {
            person.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getOccupation() != null) {
            person.setOccupation(request.getOccupation());
        }
        if (request.getGenerationLevel() != null) {
            person.setGenerationLevel(request.getGenerationLevel());
        }
        if (request.getPositionX() != null) {
            person.setPositionX(request.getPositionX());
        }
        if (request.getPositionY() != null) {
            person.setPositionY(request.getPositionY());
        }
        
        Person updatedPerson = familyTreeService.updatePerson(person, userId);
        return ResponseEntity.ok(ApiResponse.success("Person updated successfully", updatedPerson));
    }
}

