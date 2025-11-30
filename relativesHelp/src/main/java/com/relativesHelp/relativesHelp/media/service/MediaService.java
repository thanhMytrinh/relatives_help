package com.relativesHelp.relativesHelp.media.service;

import com.cloudinary.Cloudinary;
import com.relativesHelp.relativesHelp.kafka.event.MediaUploadedEvent;
import com.relativesHelp.relativesHelp.kafka.producer.KafkaEventProducer;
import com.relativesHelp.relativesHelp.media.document.MediaFile;
import com.relativesHelp.relativesHelp.media.dto.MediaResponse;
import com.relativesHelp.relativesHelp.media.dto.MediaUpdateRequest;
import com.relativesHelp.relativesHelp.media.dto.MediaUploadRequest;
import com.relativesHelp.relativesHelp.media.dto.MediaUploadResult;
import com.relativesHelp.relativesHelp.media.repository.MediaFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {
    private final MediaFileRepository mediaFileRepository;
    private final KafkaEventProducer kafkaEventProducer;
    private final Cloudinary cloudinary;

    public MediaResponse uploadMedia(MediaUploadRequest request, MultipartFile file, Long userId) {
        MediaUploadResult uploadResult = uploadToCloudinary(file, buildFolder(request.getFamilyTreeId(), userId));

        MediaFile mediaFile = MediaFile.builder()
                .familyTreeId(request.getFamilyTreeId())
                .personId(request.getPersonId())
                .uploadedByUserId(userId)
                .fileName(file.getOriginalFilename())
                .originalFileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(uploadResult.getFileSize())
                .cloudStorageUrl(uploadResult.getSecureUrl())
                .cloudinaryPublicId(uploadResult.getPublicId())
                .resourceType(uploadResult.getResourceType())
                .storageProvider("CLOUDINARY")
                .thumbnailUrl(uploadResult.getSecureUrl())
                .metadata(buildMetadata(uploadResult))
                .description(request.getDescription())
                .tags(CollectionUtils.isEmpty(request.getTags()) ? List.of() : request.getTags())
                .albumId(request.getAlbumId())
                .isPublic(Boolean.TRUE.equals(request.getIsPublic()))
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        MediaFile saved = mediaFileRepository.save(mediaFile);
        publishMediaUploadedEvent(saved);
        return MediaResponse.from(saved);
    }

    public MediaFile getMediaById(String id) {
        return mediaFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found"));
    }

    public List<MediaFile> getMediaByFamilyTreeId(Long familyTreeId) {
        return mediaFileRepository.findByFamilyTreeId(familyTreeId);
    }

    public List<MediaFile> getMediaByPersonId(Long personId) {
        return mediaFileRepository.findByPersonId(personId);
    }

    public List<MediaFile> getMediaByAlbum(String albumId) {
        return mediaFileRepository.findByAlbumId(albumId);
    }

    public List<MediaFile> listMedia(Long familyTreeId, Long personId, String albumId, boolean onlyPublic) {
        if (albumId != null) {
            return mediaFileRepository.findByAlbumId(albumId);
        }
        if (personId != null) {
            return mediaFileRepository.findByPersonId(personId);
        }
        if (familyTreeId != null) {
            return onlyPublic
                    ? mediaFileRepository.findByFamilyTreeIdAndIsPublicTrue(familyTreeId)
                    : mediaFileRepository.findByFamilyTreeId(familyTreeId);
        }
        return mediaFileRepository.findAll();
    }

    public MediaResponse updateMedia(String id, MediaUpdateRequest request, Long userId) {
        MediaFile mediaFile = getMediaById(id);
        if (request.getDescription() != null) {
            mediaFile.setDescription(request.getDescription());
        }
        if (request.getTags() != null) {
            mediaFile.setTags(request.getTags());
        }
        if (request.getAlbumId() != null) {
            mediaFile.setAlbumId(request.getAlbumId());
        }
        if (request.getIsPublic() != null) {
            mediaFile.setIsPublic(request.getIsPublic());
        }
        mediaFile.setUpdatedAt(LocalDateTime.now());
        MediaFile saved = mediaFileRepository.save(mediaFile);
        return MediaResponse.from(saved);
    }

    public void deleteMedia(String id) {
        MediaFile mediaFile = getMediaById(id);
        deleteFromCloudinary(mediaFile);
        mediaFileRepository.delete(mediaFile);
    }

    public MediaFile saveMetadata(MediaFile mediaFile) {
        if (mediaFile.getCreatedAt() == null) {
            mediaFile.setCreatedAt(LocalDateTime.now());
        }
        mediaFile.setUpdatedAt(LocalDateTime.now());
        MediaFile saved = mediaFileRepository.save(mediaFile);
        publishMediaUploadedEvent(saved);
        return saved;
    }

    private MediaUploadResult uploadToCloudinary(MultipartFile file, String folder) {
        try {
            Map<String, Object> options = Map.of(
                    "folder", folder,
                    "resource_type", "auto"
            );
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return MediaUploadResult.builder()
                    .secureUrl((String) uploadResult.get("secure_url"))
                    .publicId((String) uploadResult.get("public_id"))
                    .fileSize(((Number) uploadResult.get("bytes")).longValue())
                    .width(uploadResult.containsKey("width") ? ((Number) uploadResult.get("width")).intValue() : null)
                    .height(uploadResult.containsKey("height") ? ((Number) uploadResult.get("height")).intValue() : null)
                    .duration(uploadResult.containsKey("duration") ? ((Number) uploadResult.get("duration")).intValue() : null)
                    .resourceType((String) uploadResult.get("resource_type"))
                    .build();
        } catch (IOException e) {
            log.error("Upload to Cloudinary failed", e);
            throw new RuntimeException("Upload failed", e);
        }
    }

    private void deleteFromCloudinary(MediaFile mediaFile) {
        if (mediaFile.getCloudinaryPublicId() == null) {
            return;
        }
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("invalidate", true);
            options.put("resource_type", mediaFile.getResourceType() != null ? mediaFile.getResourceType() : "image");
            cloudinary.uploader().destroy(mediaFile.getCloudinaryPublicId(), options);
        } catch (IOException e) {
            log.warn("Failed to delete asset {} from Cloudinary", mediaFile.getId(), e);
        }
    }

    private void publishMediaUploadedEvent(MediaFile mediaFile) {
        MediaUploadedEvent event = MediaUploadedEvent.builder()
                .mediaId(mediaFile.getId())
                .familyTreeId(mediaFile.getFamilyTreeId())
                .personId(mediaFile.getPersonId())
                .uploadedByUserId(mediaFile.getUploadedByUserId())
                .fileName(mediaFile.getFileName())
                .fileType(mediaFile.getFileType())
                .fileSize(mediaFile.getFileSize())
                .cloudStorageUrl(mediaFile.getCloudStorageUrl())
                .uploadedAt(mediaFile.getCreatedAt())
                .build();
        kafkaEventProducer.publishMediaUploaded(event);
    }

    private MediaFile.MediaMetadata buildMetadata(MediaUploadResult uploadResult) {
        if (uploadResult.getWidth() == null && uploadResult.getHeight() == null && uploadResult.getDuration() == null) {
            return null;
        }
        return MediaFile.MediaMetadata.builder()
                .width(uploadResult.getWidth())
                .height(uploadResult.getHeight())
                .duration(uploadResult.getDuration())
                .build();
    }

    private String buildFolder(Long familyTreeId, Long userId) {
        if (familyTreeId != null) {
            return "family/" + familyTreeId;
        }
        return "users/" + userId;
    }
}

