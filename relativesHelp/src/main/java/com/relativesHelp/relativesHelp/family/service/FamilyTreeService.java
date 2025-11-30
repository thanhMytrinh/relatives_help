package com.relativesHelp.relativesHelp.family.service;

import com.relativesHelp.relativesHelp.family.dto.DragDropRelationshipRequest;
import com.relativesHelp.relativesHelp.family.dto.FamilyTreeStructureResponse;
import com.relativesHelp.relativesHelp.family.dto.MovePersonRequest;
import com.relativesHelp.relativesHelp.family.dto.UpdatePersonPositionRequest;
import com.relativesHelp.relativesHelp.family.entity.FamilyTree;
import com.relativesHelp.relativesHelp.family.entity.Person;
import com.relativesHelp.relativesHelp.family.entity.Relationship;
import com.relativesHelp.relativesHelp.family.repository.FamilyTreeRepository;
import com.relativesHelp.relativesHelp.family.repository.PersonRepository;
import com.relativesHelp.relativesHelp.family.repository.RelationshipRepository;
import com.relativesHelp.relativesHelp.kafka.event.PersonCreatedEvent;
import com.relativesHelp.relativesHelp.kafka.event.PersonDeletedEvent;
import com.relativesHelp.relativesHelp.kafka.event.PersonUpdatedEvent;
import com.relativesHelp.relativesHelp.kafka.event.RelationshipCreatedEvent;
import com.relativesHelp.relativesHelp.kafka.producer.KafkaEventProducer;
import com.relativesHelp.relativesHelp.media.service.MinIOService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FamilyTreeService {
    
    private final MinIOService minIOService;
    
    @Value("${minio.endpoint:http://localhost:9000}")
    private String minioEndpoint;
    
    @Value("${minio.bucket-name:relativeshelp}")
    private String bucketName;
    private final FamilyTreeRepository familyTreeRepository;
    private final PersonRepository personRepository;
    private final RelationshipRepository relationshipRepository;
    private final KafkaEventProducer kafkaEventProducer;

    public FamilyTree createFamilyTree(String name, String description, Long creatorUserId) {
        FamilyTree familyTree = FamilyTree.builder()
                .name(name)
                .description(description)
                .creatorUserId(creatorUserId)
                .isPublic(false)
                .build();
        return familyTreeRepository.save(familyTree);
    }

    public FamilyTree getFamilyTreeById(Long id) {
        return familyTreeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Family tree not found"));
    }

    public List<FamilyTree> getAllFamilyTrees() {
        return familyTreeRepository.findAll();
    }

    @Transactional
    public Person createPerson(Person person, Long createdByUserId) {
        Person savedPerson = personRepository.save(person);

        // If this is the first person in the family tree, set as root person
        FamilyTree familyTree = getFamilyTreeById(person.getFamilyTreeId());
        if (familyTree.getRootPersonId() == null) {
            familyTree.setRootPersonId(savedPerson.getId());
            familyTreeRepository.save(familyTree);
            log.info("Set first person as root: personId={}, familyTreeId={}", savedPerson.getId(), person.getFamilyTreeId());
        }

        // Publish event to Kafka asynchronously (non-blocking)
        PersonCreatedEvent event = PersonCreatedEvent.builder()
                .personId(savedPerson.getId())
                .familyTreeId(savedPerson.getFamilyTreeId())
                .fullName(savedPerson.getFullName())
                .createdByUserId(createdByUserId)
                .createdAt(LocalDateTime.now())
                .build();
        kafkaEventProducer.publishPersonCreated(event);

        log.info("Person created and event published: personId={}", savedPerson.getId());
        return savedPerson;
    }

    public List<Person> getPersonsByFamilyTreeId(Long familyTreeId) {
        return personRepository.findByFamilyTreeId(familyTreeId);
    }

    @Transactional
    public Person updatePerson(Person person, Long updatedByUserId) {
        Person savedPerson = personRepository.save(person);

        PersonUpdatedEvent event = PersonUpdatedEvent.builder()
                .personId(savedPerson.getId())
                .familyTreeId(savedPerson.getFamilyTreeId())
                .updatedByUserId(updatedByUserId)
                .updatedAt(LocalDateTime.now())
                .build();
        kafkaEventProducer.publishPersonUpdated(event);
        log.info("Person updated and event published: {}", savedPerson.getId());
        return savedPerson;
    }

    @Transactional
    public void deletePerson(Long personId, Long deletedByUserId) {
        personRepository.findById(personId).ifPresent(person -> {
            personRepository.delete(person);
            PersonDeletedEvent event = PersonDeletedEvent.builder()
                    .personId(personId)
                    .familyTreeId(person.getFamilyTreeId())
                    .deletedByUserId(deletedByUserId)
                    .deletedAt(LocalDateTime.now())
                    .build();
            kafkaEventProducer.publishPersonDeleted(event);
            log.info("Person deleted and event published: {}", personId);
        });
    }

    @Transactional
    public Relationship createRelationship(Relationship relationship, Long createdByUserId) {
        Relationship savedRelationship = relationshipRepository.save(relationship);

        // Publish event to Kafka asynchronously (non-blocking)
        RelationshipCreatedEvent event = RelationshipCreatedEvent.builder()
                .relationshipId(savedRelationship.getId())
                .familyTreeId(savedRelationship.getFamilyTreeId())
                .personId(savedRelationship.getPersonId())
                .relatedPersonId(savedRelationship.getRelatedPersonId())
                .relationshipType(savedRelationship.getRelationshipType().name())
                .createdByUserId(createdByUserId)
                .createdAt(LocalDateTime.now())
                .build();
        kafkaEventProducer.publishRelationshipCreated(event);

        log.info("Relationship created and event published: relationshipId={}", 
            savedRelationship.getId());
        return savedRelationship;
    }

    public List<Relationship> getRelationshipsByPersonId(Long personId, Long familyTreeId) {
        return relationshipRepository.findAllRelationshipsForPerson(familyTreeId, personId);
    }

    /**
     * Get family tree structure for drag & drop visualization
     */
    public FamilyTreeStructureResponse getFamilyTreeStructure(Long familyTreeId) {
        List<Person> persons = personRepository.findByFamilyTreeId(familyTreeId);
        List<Relationship> relationships = relationshipRepository.findByFamilyTreeId(familyTreeId);
        
        log.info("Getting family tree structure: familyTreeId={}, personsCount={}, relationshipsCount={}", 
                familyTreeId, persons.size(), relationships.size());
        
        if (persons.isEmpty()) {
            log.warn("No persons found for familyTreeId={}", familyTreeId);
            return null;
        }
        
        // Find root person
        FamilyTree tree = getFamilyTreeById(familyTreeId);
        Person rootPerson = null;
        if (tree.getRootPersonId() != null) {
            rootPerson = personRepository.findById(tree.getRootPersonId()).orElse(null);
            log.info("Root person from tree: rootPersonId={}, found={}", tree.getRootPersonId(), rootPerson != null);
        }
        if (rootPerson == null) {
            rootPerson = findRootPerson(persons, relationships);
            log.info("Root person from findRootPerson: found={}", rootPerson != null);
        }
        if (rootPerson == null && !persons.isEmpty()) {
            // Use first person as root if no root is set
            rootPerson = persons.get(0);
            // Auto-set as root for future
            tree.setRootPersonId(rootPerson.getId());
            familyTreeRepository.save(tree);
            log.info("Auto-set first person as root: personId={}, familyTreeId={}", rootPerson.getId(), familyTreeId);
        }
        
        if (rootPerson == null) {
            log.error("Cannot find root person for familyTreeId={}", familyTreeId);
            return null;
        }
        
        log.info("Building tree structure for root person: personId={}, name={}", rootPerson.getId(), rootPerson.getFullName());
        
        // Build tree structure starting from root with visited set to prevent cycles
        Set<Long> visited = new HashSet<>();
        FamilyTreeStructureResponse rootStructure = buildTreeStructure(rootPerson, persons, relationships, visited);
        
        if (rootStructure == null) {
            log.error("Failed to build tree structure for root person: personId={}", rootPerson.getId());
            return null;
        }
        
        log.info("Tree structure built successfully: rootId={}, rootName={}", rootStructure.getId(), rootStructure.getName());
        
        // Add orphan nodes (persons without relationships) as separate nodes
        Set<Long> connectedPersonIds = new HashSet<>();
        collectConnectedPersonIds(rootStructure, connectedPersonIds);
        
        List<Person> orphanPersons = persons.stream()
                .filter(p -> !connectedPersonIds.contains(p.getId()))
                .collect(Collectors.toList());
        
        if (!orphanPersons.isEmpty()) {
            log.info("Found {} orphan persons (not connected to root)", orphanPersons.size());
            
            // Add orphan persons as children of root structure for visualization
            // This allows them to be displayed in the tree even without relationships
            // Use a new visited set for orphans to avoid conflicts
            Set<Long> orphanVisited = new HashSet<>(connectedPersonIds);
            List<FamilyTreeStructureResponse> orphanStructures = orphanPersons.stream()
                    .map(orphan -> buildTreeStructure(orphan, persons, relationships, orphanVisited))
                    .filter(orphan -> orphan != null)
                    .collect(Collectors.toList());
            
            // Merge orphan structures into root's children
            if (rootStructure.getChildren() == null) {
                rootStructure.setChildren(new java.util.ArrayList<>());
            }
            rootStructure.getChildren().addAll(orphanStructures);
            
            log.info("Added {} orphan persons to tree structure", orphanStructures.size());
        }
        
        return rootStructure;
    }
    
    private void collectConnectedPersonIds(FamilyTreeStructureResponse node, Set<Long> ids) {
        if (node == null) return;
        ids.add(node.getId());
        if (node.getChildren() != null) {
            node.getChildren().forEach(child -> collectConnectedPersonIds(child, ids));
        }
        if (node.getSpouses() != null) {
            node.getSpouses().forEach(spouse -> collectConnectedPersonIds(spouse, ids));
        }
    }

    /**
     * Update relationship via drag & drop
     */
    @Transactional
    public Relationship updateRelationshipByDragDrop(Long familyTreeId, DragDropRelationshipRequest request, Long userId) {
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new RuntimeException("Person not found"));
        
        Person targetPerson = personRepository.findById(request.getTargetPersonId())
                .orElseThrow(() -> new RuntimeException("Target person not found"));
        
        if (!person.getFamilyTreeId().equals(familyTreeId) || 
            !targetPerson.getFamilyTreeId().equals(familyTreeId)) {
            throw new RuntimeException("Persons must belong to the same family tree");
        }
        
        Relationship relationship;
        
        switch (request.getAction()) {
            case CREATE:
                // Check if relationship already exists
                relationship = relationshipRepository.findByPersonIdAndRelationshipType(
                    request.getPersonId(), request.getRelationshipType())
                    .stream()
                    .filter(r -> r.getRelatedPersonId().equals(request.getTargetPersonId()) 
                        && r.getFamilyTreeId().equals(familyTreeId))
                    .findFirst()
                    .orElse(null);
                
                if (relationship == null) {
                    // Create new relationship
                    relationship = Relationship.builder()
                            .familyTreeId(familyTreeId)
                            .personId(request.getPersonId())
                            .relatedPersonId(request.getTargetPersonId())
                            .relationshipType(request.getRelationshipType())
                            .build();
                    
                    // Save relationship
                    relationship = relationshipRepository.save(relationship);
                    log.info("Created relationship: id={}, personId={}, relatedPersonId={}, type={}", 
                        relationship.getId(), relationship.getPersonId(), 
                        relationship.getRelatedPersonId(), relationship.getRelationshipType());
                    
                    // Publish event (createRelationship already saves, so we just publish event here)
                    RelationshipCreatedEvent event = RelationshipCreatedEvent.builder()
                            .relationshipId(relationship.getId())
                            .familyTreeId(relationship.getFamilyTreeId())
                            .personId(relationship.getPersonId())
                            .relatedPersonId(relationship.getRelatedPersonId())
                            .relationshipType(relationship.getRelationshipType().name())
                            .createdByUserId(userId)
                            .createdAt(LocalDateTime.now())
                            .build();
                    kafkaEventProducer.publishRelationshipCreated(event);
                    log.info("Published relationship created event: relationshipId={}", relationship.getId());
                } else {
                    log.warn("Relationship already exists: id={}, personId={}, relatedPersonId={}, type={}", 
                        relationship.getId(), relationship.getPersonId(), 
                        relationship.getRelatedPersonId(), relationship.getRelationshipType());
                }
                break;
                
            case UPDATE:
                relationship = relationshipRepository.findByPersonIdAndRelationshipType(
                    request.getPersonId(), request.getRelationshipType())
                    .stream()
                    .filter(r -> r.getRelatedPersonId().equals(request.getTargetPersonId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Relationship not found"));
                
                relationship.setRelationshipType(request.getRelationshipType());
                relationship = relationshipRepository.save(relationship);
                break;
                
            case DELETE:
                relationship = relationshipRepository.findByPersonIdAndRelationshipType(
                    request.getPersonId(), request.getRelationshipType())
                    .stream()
                    .filter(r -> r.getRelatedPersonId().equals(request.getTargetPersonId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Relationship not found"));
                
                relationshipRepository.delete(relationship);
                return relationship;
                
            default:
                throw new RuntimeException("Invalid action type");
        }
        
        if (request.getPositionX() != null && request.getPositionY() != null) {
            person.setPositionX(request.getPositionX());
            person.setPositionY(request.getPositionY());
            personRepository.save(person);
        }
        
        return relationship;
    }

    /**
     * Move person to new parent
     */
    @Transactional
    public Person movePersonToParent(Long familyTreeId, Long personId, MovePersonRequest request, Long userId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));
        
        Person newParent = personRepository.findById(request.getNewParentId())
                .orElseThrow(() -> new RuntimeException("New parent not found"));
        
        if (!person.getFamilyTreeId().equals(familyTreeId) || 
            !newParent.getFamilyTreeId().equals(familyTreeId)) {
            throw new RuntimeException("Persons must belong to the same family tree");
        }
        
        // Remove old parent relationships
        List<Relationship> oldParentRelationships = relationshipRepository
                .findByRelatedPersonId(personId)
                .stream()
                .filter(r -> r.getRelationshipType() == Relationship.RelationshipType.FATHER ||
                           r.getRelationshipType() == Relationship.RelationshipType.MOTHER)
                .collect(Collectors.toList());
        
        relationshipRepository.deleteAll(oldParentRelationships);
        
        // Create new parent relationship
        Relationship.RelationshipType relationshipType = request.getRelationshipType() != null
                ? request.getRelationshipType()
                : (newParent.getGender() == Person.Gender.MALE 
                    ? Relationship.RelationshipType.FATHER 
                    : Relationship.RelationshipType.MOTHER);
        
        Relationship newRelationship = Relationship.builder()
                .familyTreeId(familyTreeId)
                .personId(newParent.getId())
                .relatedPersonId(personId)
                .relationshipType(relationshipType)
                .build();
        
        createRelationship(newRelationship, userId);
        
        if (request.getPositionX() != null && request.getPositionY() != null) {
            person.setPositionX(request.getPositionX());
            person.setPositionY(request.getPositionY());
        }
        
        person.setGenerationLevel(newParent.getGenerationLevel() + 1);
        
        return personRepository.save(person);
    }

    /**
     * Update person position
     */
    @Transactional
    public Person updatePersonPosition(Long familyTreeId, Long personId, UpdatePersonPositionRequest request, Long userId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));
        
        if (!person.getFamilyTreeId().equals(familyTreeId)) {
            throw new RuntimeException("Person does not belong to this family tree");
        }
        
        person.setPositionX(request.getPositionX());
        person.setPositionY(request.getPositionY());
        
        if (request.getGenerationLevel() != null) {
            person.setGenerationLevel(request.getGenerationLevel());
        }
        
        return updatePerson(person, userId);
    }

    private Person findRootPerson(List<Person> persons, List<Relationship> relationships) {
        Map<Long, Boolean> hasParent = new HashMap<>();
        for (Relationship rel : relationships) {
            if (rel.getRelationshipType() == Relationship.RelationshipType.FATHER ||
                rel.getRelationshipType() == Relationship.RelationshipType.MOTHER) {
                hasParent.put(rel.getRelatedPersonId(), true);
            }
        }
        
        return persons.stream()
                .filter(p -> !hasParent.containsKey(p.getId()))
                .findFirst()
                .orElse(null);
    }

    private FamilyTreeStructureResponse buildTreeStructure(Person person, List<Person> allPersons, List<Relationship> allRelationships, Set<Long> visited) {
        // Prevent infinite recursion by checking if person was already visited
        if (visited.contains(person.getId())) {
            log.warn("Circular relationship detected for person: id={}, name={}. Skipping to prevent infinite loop.", 
                person.getId(), person.getFullName());
            return null;
        }
        
        // Mark this person as visited
        visited.add(person.getId());
        
        try {
            List<FamilyTreeStructureResponse> children = allRelationships.stream()
                    .filter(r -> r.getPersonId().equals(person.getId()) &&
                               (r.getRelationshipType() == Relationship.RelationshipType.SON ||
                                r.getRelationshipType() == Relationship.RelationshipType.DAUGHTER))
                    .map(r -> {
                        Person child = allPersons.stream()
                                .filter(p -> p.getId().equals(r.getRelatedPersonId()))
                                .findFirst()
                                .orElse(null);
                        return child != null ? buildTreeStructure(child, allPersons, allRelationships, visited) : null;
                    })
                    .filter(c -> c != null)
                    .collect(Collectors.toList());
            
            List<FamilyTreeStructureResponse> spouses = allRelationships.stream()
                    .filter(r -> (r.getPersonId().equals(person.getId()) || r.getRelatedPersonId().equals(person.getId())) &&
                               r.getRelationshipType() == Relationship.RelationshipType.SPOUSE)
                    .map(r -> {
                        Long spouseId = r.getPersonId().equals(person.getId()) 
                                ? r.getRelatedPersonId() 
                                : r.getPersonId();
                        Person spouse = allPersons.stream()
                                .filter(p -> p.getId().equals(spouseId))
                                .findFirst()
                                .orElse(null);
                        // For spouses, create a simple structure without recursion to prevent cycles
                        if (spouse != null && !visited.contains(spouse.getId())) {
                            // Mark spouse as visited temporarily
                            visited.add(spouse.getId());
                            try {
                                String spouseAvatarUrl = convertToMinIOUrl(spouse.getAvatarUrl());
                                return FamilyTreeStructureResponse.builder()
                                        .id(spouse.getId())
                                        .name(spouse.getFullName())
                                        .fullName(spouse.getFullName())
                                        .avatarUrl(spouseAvatarUrl)
                                        .gender(FamilyTreeStructureResponse.Gender.valueOf(spouse.getGender().name()))
                                        .dateOfBirth(spouse.getDateOfBirth())
                                        .dateOfDeath(spouse.getDateOfDeath())
                                        .isAlive(spouse.getIsAlive())
                                        .generationLevel(spouse.getGenerationLevel())
                                        .positionX(spouse.getPositionX())
                                        .positionY(spouse.getPositionY())
                                        .children(new ArrayList<>()) // Empty children to prevent recursion
                                        .spouses(new ArrayList<>()) // Empty spouses to prevent recursion
                                        .parentIds(new ArrayList<>())
                                        .build();
                            } finally {
                                // Remove spouse from visited after building structure
                                visited.remove(spouse.getId());
                            }
                        }
                        return null;
                    })
                    .filter(s -> s != null)
                    .collect(Collectors.toList());
            
            List<Long> parentIds = allRelationships.stream()
                    .filter(r -> r.getRelatedPersonId().equals(person.getId()) &&
                               (r.getRelationshipType() == Relationship.RelationshipType.FATHER ||
                                r.getRelationshipType() == Relationship.RelationshipType.MOTHER))
                    .map(Relationship::getPersonId)
                    .collect(Collectors.toList());
            
            // Convert avatarUrl to full MinIO URL if needed
            String avatarUrl = convertToMinIOUrl(person.getAvatarUrl());
            
            return FamilyTreeStructureResponse.builder()
                    .id(person.getId())
                    .name(person.getFullName())
                    .fullName(person.getFullName())
                    .avatarUrl(avatarUrl)
                    .gender(FamilyTreeStructureResponse.Gender.valueOf(person.getGender().name()))
                    .dateOfBirth(person.getDateOfBirth())
                    .dateOfDeath(person.getDateOfDeath())
                    .isAlive(person.getIsAlive())
                    .generationLevel(person.getGenerationLevel())
                    .positionX(person.getPositionX())
                    .positionY(person.getPositionY())
                    .children(children)
                    .spouses(spouses)
                    .parentIds(parentIds)
                    .build();
        } finally {
            // Keep person in visited set to prevent cycles
            // Don't remove to prevent infinite recursion
        }

    }

    
    /**
     * Convert avatarUrl to full MinIO URL
     * If avatarUrl is already a full URL, return as is
     * If avatarUrl is an object name (path), convert to full MinIO URL
     * If avatarUrl is null or empty, return null
     */
    private String convertToMinIOUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return null;
        }
        
        String trimmedUrl = avatarUrl.trim();
        
        // If already a full URL (starts with http:// or https://), return as is
        if (trimmedUrl.startsWith("http://") || trimmedUrl.startsWith("https://")) {
            return trimmedUrl;
        }
        
        // If it's an object name (path), convert to full MinIO URL
        // Remove leading slash if present
        String objectName = trimmedUrl.startsWith("/") ? trimmedUrl.substring(1) : trimmedUrl;
        
        // Generate full MinIO URL
        String fullUrl = minioEndpoint + "/" + bucketName + "/" + objectName;
        log.debug("Converted avatarUrl from '{}' to '{}'", avatarUrl, fullUrl);
        
        return fullUrl;
    }
}

