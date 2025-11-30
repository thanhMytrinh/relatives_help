package com.relativesHelp.relativesHelp.media.repository;

import com.relativesHelp.relativesHelp.media.document.ActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {
    List<ActivityLog> findByFamilyTreeId(Long familyTreeId);
    List<ActivityLog> findByUserId(Long userId);
    List<ActivityLog> findByFamilyTreeIdAndTimestampBetween(Long familyTreeId, 
                                                             LocalDateTime start, 
                                                             LocalDateTime end);
}

