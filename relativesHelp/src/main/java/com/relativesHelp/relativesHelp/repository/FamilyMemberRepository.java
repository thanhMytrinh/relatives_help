package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// 3. FamilyMemberRepository.java (QUAN TRỌNG NHẤT)
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    List<FamilyMember> findByFamilyId(Long familyId);
    List<FamilyMember> findByFamilyIdAndAliveTrue(Long familyId);
    List<FamilyMember> findByFamilyIdOrderByGenerationAscDisplayOrderAsc(Long familyId);
    List<FamilyMember> findByFamilyIdAndGeneration(Long familyId, int generation);

    // Tìm cha mẹ
    @Query("SELECT p.parent FROM ParentChildRelationship p WHERE p.child.id = :childId")
    List<FamilyMember> findParentsByChildId(@Param("childId") Long childId);

    // Tìm con cái
    @Query("SELECT p.child FROM ParentChildRelationship p WHERE p.parent.id = :parentId")
    List<FamilyMember> findChildrenByParentId(@Param("parentId") Long parentId);

    // Tìm anh chị em ruột (cùng cha hoặc cùng mẹ)
    @Query("SELECT DISTINCT c FROM ParentChildRelationship p1 " +
            "JOIN ParentChildRelationship p2 ON p1.parent.id = p2.parent.id " +
            "JOIN FamilyMember c ON p2.child.id = c.id " +
            "WHERE p1.child.id = :memberId AND c.id != :memberId")
    List<FamilyMember> findSiblings(@Param("memberId") Long memberId);
}