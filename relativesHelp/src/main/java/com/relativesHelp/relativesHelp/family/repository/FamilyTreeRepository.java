package com.relativesHelp.relativesHelp.family.repository;

import com.relativesHelp.relativesHelp.family.entity.FamilyTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyTreeRepository extends JpaRepository<FamilyTree, Long> {
    List<FamilyTree> findByCreatorUserId(Long creatorUserId);
    List<FamilyTree> findByIsPublicTrue();
}

