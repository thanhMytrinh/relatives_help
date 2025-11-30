package com.relativesHelp.relativesHelp.family.repository;

import com.relativesHelp.relativesHelp.family.entity.Relationship;
import com.relativesHelp.relativesHelp.family.entity.Relationship.RelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
    List<Relationship> findByPersonId(Long personId);
    List<Relationship> findByRelatedPersonId(Long relatedPersonId);
    List<Relationship> findByFamilyTreeId(Long familyTreeId);
    List<Relationship> findByPersonIdAndRelationshipType(Long personId, RelationshipType type);
    
    @Query("SELECT r FROM Relationship r WHERE r.familyTreeId = :familyTreeId AND " +
           "(r.personId = :personId OR r.relatedPersonId = :personId)")
    List<Relationship> findAllRelationshipsForPerson(@Param("familyTreeId") Long familyTreeId, 
                                                      @Param("personId") Long personId);
}

