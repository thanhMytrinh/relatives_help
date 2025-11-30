package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.MemberPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberPhotoRepository extends JpaRepository<MemberPhoto, Long> {
    List<MemberPhoto> findByMemberId(Long memberId);
    Optional<MemberPhoto> findByMemberIdAndPrimaryTrue(Long memberId);
}