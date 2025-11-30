package com.relativesHelp.relativesHelp.media.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "activity_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    private String id;

    @Indexed
    private Long familyTreeId;

    @Indexed
    private Long userId;

    private String action;
    private String description;
    private Map<String, Object> metadata;

    @Indexed
    private LocalDateTime timestamp;
}

