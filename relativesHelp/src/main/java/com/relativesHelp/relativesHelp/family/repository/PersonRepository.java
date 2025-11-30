package com.relativesHelp.relativesHelp.family.repository;

import com.relativesHelp.relativesHelp.family.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByFamilyTreeId(Long familyTreeId);
    List<Person> findByUserId(Long userId);
    List<Person> findByFamilyTreeIdAndGenerationLevel(Long familyTreeId, Integer generationLevel);
    
    @Query("SELECT p FROM Person p WHERE p.familyTreeId = :familyTreeId AND " +
           "(LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.biography) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Person> searchByKeyword(@Param("familyTreeId") Long familyTreeId, 
                                  @Param("keyword") String keyword);
}

