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

@Document(collection = "family_tree_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyTreeDocument {
    @Id
    private String id;

    @Indexed
    private Long familyTreeId;

    private DocumentType documentType;
    private String title;

    private DocumentContent content;

    private List<Attachment> attachments;

    @Indexed
    private Long createdByUserId;

    private Integer version;

    @Indexed
    private Boolean isPublic;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum DocumentType {
        GENEALOGY_BOOK, CERTIFICATE, WILL, OTHER
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentContent {
        private String format; // markdown, html, pdf_url
        private String data;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attachment {
        private String fileId;
        private String fileName;
        private String url;
    }
}

