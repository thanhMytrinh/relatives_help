package com.relativesHelp.relativesHelp.repository;

import com.relativesHelp.relativesHelp.entity.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    Page<Media> findByFamilyId(Long familyId, Pageable pageable);
    List<Media> findByFamilyIdAndFileType(Long familyId, Media.FileType fileType);
    List<Media> findByUploadedById(Long userId);
}