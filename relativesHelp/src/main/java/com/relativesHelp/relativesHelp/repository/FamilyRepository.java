package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.Family;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyRepository extends JpaRepository<Family, Long> {
    List<Family> findBySurnameContainingIgnoreCase(String surname);
    List<Family> findByCreatedById(Long userId);
    Page<Family> findByPublicFamilyTrue(Pageable pageable);
    Page<Family> findByNameContainingIgnoreCase(String name, Pageable pageable);
}