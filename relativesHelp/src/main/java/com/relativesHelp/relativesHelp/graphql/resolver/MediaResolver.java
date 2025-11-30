package com.relativesHelp.relativesHelp.graphql.resolver;

import com.relativesHelp.relativesHelp.media.document.Album;
import com.relativesHelp.relativesHelp.media.document.MediaFile;
import com.relativesHelp.relativesHelp.media.repository.MediaFileRepository;
import com.relativesHelp.relativesHelp.media.repository.AlbumDocumentRepository;
import com.relativesHelp.relativesHelp.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MediaResolver {
    private final MediaService mediaService;
    private final MediaFileRepository mediaFileRepository;
    private final AlbumDocumentRepository albumRepository;

    @QueryMapping
    public List<MediaFile> media(@Argument Long familyTreeId) {
        return mediaService.getMediaByFamilyTreeId(familyTreeId);
    }

    @QueryMapping
    public List<MediaFile> mediaByPerson(@Argument Long personId) {
        return mediaService.getMediaByPersonId(personId);
    }

    @QueryMapping
    public Album album(@Argument String id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));
    }

    @QueryMapping
    public List<Album> albums(@Argument Long familyTreeId) {
        return albumRepository.findByFamilyTreeId(familyTreeId);
    }

    @MutationMapping
    public MediaFile uploadMedia(
            @Argument("input") UploadMediaInput input,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        MediaFile mediaFile = MediaFile.builder()
                .familyTreeId(input.familyTreeId())
                .personId(input.personId())
                .uploadedByUserId(userId)
                .fileName(input.fileName())
                .originalFileName(input.originalFileName())
                .fileType(input.fileType())
                .fileSize(input.fileSize())
                .cloudStorageUrl(input.cloudStorageUrl())
                .thumbnailUrl(input.thumbnailUrl())
                .description(input.description())
                .tags(input.tags() != null ? input.tags() : List.of())
                .albumId(input.albumId())
                .isPublic(false)
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return mediaService.saveMetadata(mediaFile);
    }

    @MutationMapping
    public Album createAlbum(
            @Argument("input") CreateAlbumInput input,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        Album album = Album.builder()
                .familyTreeId(input.familyTreeId())
                .name(input.name())
                .description(input.description())
                .coverImageId(input.coverImageId())
                .createdByUserId(userId)
                .mediaCount(0)
                .isPublic(false)
                .tags(input.tags() != null ? input.tags() : List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return albumRepository.save(album);
    }

    @MutationMapping
    public Boolean deleteMedia(@Argument String id) {
        mediaFileRepository.deleteById(id);
        return true;
    }

    // Input Records
    public record UploadMediaInput(
            Long familyTreeId,
            Long personId,
            String fileName,
            String originalFileName,
            String fileType,
            Long fileSize,
            String cloudStorageUrl,
            String thumbnailUrl,
            String description,
            String albumId,
            List<String> tags
    ) {}

    public record CreateAlbumInput(
            Long familyTreeId,
            String name,
            String description,
            String coverImageId,
            List<String> tags
    ) {}
}


