package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.MarriageMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarriageMemberRepository extends JpaRepository<MarriageMember, Long> {
    List<MarriageMember> findByMemberId(Long memberId);
    List<MarriageMember> findByMarriageId(Long marriageId);
}