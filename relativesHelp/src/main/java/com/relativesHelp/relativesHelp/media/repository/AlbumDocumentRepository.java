package com.relativesHelp.relativesHelp.media.repository;

import com.relativesHelp.relativesHelp.media.document.Album;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumDocumentRepository extends MongoRepository<Album, String> {
    List<Album> findByFamilyTreeId(Long familyTreeId);
    List<Album> findByCreatedByUserId(Long createdByUserId);
    List<Album> findByFamilyTreeIdAndIsPublicTrue(Long familyTreeId);
}


