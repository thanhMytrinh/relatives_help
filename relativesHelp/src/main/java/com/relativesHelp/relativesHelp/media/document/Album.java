package com.relativesHelp.relativesHelp.media.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "albums")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Album {
    @Id
    private String id;

    @Indexed
    private Long familyTreeId;

    private String name;
    private String description;

    private String coverImageId;

    @Indexed
    private Long createdByUserId;

    private Integer mediaCount;

    @Indexed
    private Boolean isPublic;

    private List<String> tags;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

