package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.ParentChildRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParentChildRelationshipRepository extends JpaRepository<ParentChildRelationship, Long> {
    List<ParentChildRelationship> findByParentId(Long parentId);
    List<ParentChildRelationship> findByChildId(Long childId);
    Optional<ParentChildRelationship> findByParentIdAndChildId(Long parentId, Long childId);
    void deleteByParentIdAndChildId(Long parentId, Long childId);
}