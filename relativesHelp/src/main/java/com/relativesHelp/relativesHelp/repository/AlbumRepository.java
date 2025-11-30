package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByFamilyId(Long familyId);
    List<Album> findByFamilyIdAndCreatedById(Long familyId, Long userId);
}