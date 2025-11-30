package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.Marriage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarriageRepository extends JpaRepository<Marriage, Long> {
    List<Marriage> findByFamilyId(Long familyId);
}