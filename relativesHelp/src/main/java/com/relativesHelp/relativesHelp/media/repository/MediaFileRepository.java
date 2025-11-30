package com.relativesHelp.relativesHelp.media.repository;

import com.relativesHelp.relativesHelp.media.document.MediaFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaFileRepository extends MongoRepository<MediaFile, String> {
    List<MediaFile> findByFamilyTreeId(Long familyTreeId);
    List<MediaFile> findByPersonId(Long personId);
    List<MediaFile> findByAlbumId(String albumId);
    List<MediaFile> findByFamilyTreeIdAndIsPublicTrue(Long familyTreeId);
}

