package com.relativesHelp.relativesHelp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "media")
@Getter
@Setter
@NoArgsConstructor
public class Media {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 1000)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    private String mimeType;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private StorageProvider storageProvider = StorageProvider.S3;

    @Column(name = "is_public")
    private boolean publicMedia = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum FileType { IMAGE, VIDEO, DOCUMENT }
    public enum StorageProvider { S3, MINIO, LOCAL }
}