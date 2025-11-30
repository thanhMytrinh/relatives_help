package com.relativesHelp.relativesHelp.family.repository;

import com.relativesHelp.relativesHelp.family.entity.FamilyTreeMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyTreeMemberRepository extends JpaRepository<FamilyTreeMember, Long> {
    List<FamilyTreeMember> findByFamilyTreeId(Long familyTreeId);
    List<FamilyTreeMember> findByUserId(Long userId);
    Optional<FamilyTreeMember> findByFamilyTreeIdAndUserId(Long familyTreeId, Long userId);
    boolean existsByFamilyTreeIdAndUserId(Long familyTreeId, Long userId);
}

