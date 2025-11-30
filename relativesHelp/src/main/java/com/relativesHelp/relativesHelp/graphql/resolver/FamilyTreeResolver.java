package com.relativesHelp.relativesHelp.graphql.resolver;

import com.relativesHelp.relativesHelp.family.entity.FamilyTree;
import com.relativesHelp.relativesHelp.family.entity.Person;
import com.relativesHelp.relativesHelp.family.entity.Relationship;
import com.relativesHelp.relativesHelp.family.repository.PersonRepository;
import com.relativesHelp.relativesHelp.family.repository.RelationshipRepository;
import com.relativesHelp.relativesHelp.family.service.FamilyTreeService;
import com.relativesHelp.relativesHelp.search.document.PersonSearchDocument;
import com.relativesHelp.relativesHelp.search.service.SearchQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FamilyTreeResolver {
    private final FamilyTreeService familyTreeService;
    private final PersonRepository personRepository;
    private final RelationshipRepository relationshipRepository;
    private final SearchQueryService searchQueryService;

    @QueryMapping
    public FamilyTree familyTree(@Argument Long id) {
        return familyTreeService.getFamilyTreeById(id);
    }

    @QueryMapping
    public List<FamilyTree> familyTrees(Authentication authentication) {
        // Return all family trees (you can filter by user membership later)
        return familyTreeService.getAllFamilyTrees();
    }

    @QueryMapping
    public List<Person> persons(@Argument Long familyTreeId) {
        return familyTreeService.getPersonsByFamilyTreeId(familyTreeId);
    }

    @QueryMapping
    public Person person(@Argument Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found"));
    }

    @QueryMapping
    public List<Relationship> relationships(
            @Argument Long personId,
            @Argument Long familyTreeId) {
        return familyTreeService.getRelationshipsByPersonId(personId, familyTreeId);
    }

    @QueryMapping
    public List<Person> searchPersons(
            @Argument Long familyTreeId,
            @Argument String keyword) {
        Page<PersonSearchDocument> results = searchQueryService.searchPersons(
                familyTreeId,
                keyword,
                PageRequest.of(0, 50)
        );
        return results.stream()
                .map(PersonSearchDocument::toPersonEntity)
                .toList();
    }

    @MutationMapping
    public FamilyTree createFamilyTree(
            @Argument("input") CreateFamilyTreeInput input,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return familyTreeService.createFamilyTree(
                input.name(),
                input.description(),
                userId
        );
    }

    @MutationMapping
    public Person createPerson(
            @Argument("input") CreatePersonInput input,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        Person person = Person.builder()
                .familyTreeId(input.familyTreeId())
                .userId(input.userId())
                .fullName(input.fullName())
                .gender(Person.Gender.valueOf(input.gender().name()))
                .dateOfBirth(input.dateOfBirth() != null ? 
                    LocalDate.parse(input.dateOfBirth()) : null)
                .dateOfDeath(input.dateOfDeath() != null ? 
                    LocalDate.parse(input.dateOfDeath()) : null)
                .placeOfBirth(input.placeOfBirth())
                .placeOfDeath(input.placeOfDeath())
                .isAlive(input.isAlive() != null ? input.isAlive() : true)
                .biography(input.biography())
                .avatarUrl(input.avatarUrl())
                .occupation(input.occupation())
                .generationLevel(input.generationLevel() != null ? 
                    input.generationLevel() : 0)
                .build();

        return familyTreeService.createPerson(person, userId);
    }

    @MutationMapping
    public Person updatePerson(
            @Argument Long id,
            @Argument("input") UpdatePersonInput input,
            Authentication authentication) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        if (input.fullName() != null) person.setFullName(input.fullName());
        if (input.gender() != null) person.setGender(Person.Gender.valueOf(input.gender().name()));
        if (input.dateOfBirth() != null) person.setDateOfBirth(LocalDate.parse(input.dateOfBirth()));
        if (input.dateOfDeath() != null) person.setDateOfDeath(LocalDate.parse(input.dateOfDeath()));
        if (input.placeOfBirth() != null) person.setPlaceOfBirth(input.placeOfBirth());
        if (input.placeOfDeath() != null) person.setPlaceOfDeath(input.placeOfDeath());
        if (input.isAlive() != null) person.setIsAlive(input.isAlive());
        if (input.biography() != null) person.setBiography(input.biography());
        if (input.avatarUrl() != null) person.setAvatarUrl(input.avatarUrl());
        if (input.occupation() != null) person.setOccupation(input.occupation());
        if (input.generationLevel() != null) person.setGenerationLevel(input.generationLevel());

        Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
        return familyTreeService.updatePerson(person, userId);
    }

    @MutationMapping
    public Boolean deletePerson(@Argument Long id, Authentication authentication) {
        Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
        familyTreeService.deletePerson(id, userId);
        return true;
    }

    @MutationMapping
    public Relationship createRelationship(
            @Argument("input") CreateRelationshipInput input,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        Relationship relationship = Relationship.builder()
                .familyTreeId(input.familyTreeId())
                .personId(input.personId())
                .relatedPersonId(input.relatedPersonId())
                .relationshipType(Relationship.RelationshipType.valueOf(
                    input.relationshipType().name()))
                .marriageDate(input.marriageDate() != null ? 
                    LocalDate.parse(input.marriageDate()) : null)
                .divorceDate(input.divorceDate() != null ? 
                    LocalDate.parse(input.divorceDate()) : null)
                .notes(input.notes())
                .build();

        return familyTreeService.createRelationship(relationship, userId);
    }

    @SchemaMapping(typeName = "Relationship", field = "person")
    public Person getPerson(Relationship relationship) {
        return personRepository.findById(relationship.getPersonId())
                .orElse(null);
    }

    @SchemaMapping(typeName = "Relationship", field = "relatedPerson")
    public Person getRelatedPerson(Relationship relationship) {
        return personRepository.findById(relationship.getRelatedPersonId())
                .orElse(null);
    }

    // Input Records
    public record CreateFamilyTreeInput(
            String name,
            String description,
            Boolean isPublic
    ) {}

    public record CreatePersonInput(
            Long familyTreeId,
            Long userId,
            String fullName,
            Person.Gender gender,
            String dateOfBirth,
            String dateOfDeath,
            String placeOfBirth,
            String placeOfDeath,
            Boolean isAlive,
            String biography,
            String avatarUrl,
            String occupation,
            Integer generationLevel
    ) {}

    public record UpdatePersonInput(
            String fullName,
            Person.Gender gender,
            String dateOfBirth,
            String dateOfDeath,
            String placeOfBirth,
            String placeOfDeath,
            Boolean isAlive,
            String biography,
            String avatarUrl,
            String occupation,
            Integer generationLevel
    ) {}

    public record CreateRelationshipInput(
            Long familyTreeId,
            Long personId,
            Long relatedPersonId,
            Relationship.RelationshipType relationshipType,
            String marriageDate,
            String divorceDate,
            String notes
    ) {}
}


