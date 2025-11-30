package com.relativesHelp.relativesHelp.media.repository;

import com.relativesHelp.relativesHelp.media.document.FamilyTreeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyTreeDocumentRepository extends MongoRepository<FamilyTreeDocument, String> {
    List<FamilyTreeDocument> findByFamilyTreeId(Long familyTreeId);
    List<FamilyTreeDocument> findByCreatedByUserId(Long createdByUserId);
    List<FamilyTreeDocument> findByFamilyTreeIdAndIsPublicTrue(Long familyTreeId);
}

