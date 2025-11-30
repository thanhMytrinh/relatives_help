package com.relativesHelp.relativesHelp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "album_media", uniqueConstraints = {
        @UniqueConstraint(name = "uq_album_media", columnNames = {"album_id", "media_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class AlbumMedia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @Column(name = "order_index")
    private int orderIndex = 0;

    @CreationTimestamp
    @Column(name = "added_at")
    private LocalDateTime addedAt;
}