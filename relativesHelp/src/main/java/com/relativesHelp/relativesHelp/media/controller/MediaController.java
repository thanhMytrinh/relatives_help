package com.relativesHelp.relativesHelp.media.controller;

import com.relativesHelp.relativesHelp.common.dto.ApiResponse;
import com.relativesHelp.relativesHelp.media.document.MediaFile;
import com.relativesHelp.relativesHelp.media.dto.CloudinaryFolderDto;
import com.relativesHelp.relativesHelp.media.dto.CloudinaryResourceDto;
import com.relativesHelp.relativesHelp.media.dto.MediaResponse;
import com.relativesHelp.relativesHelp.media.dto.MediaUpdateRequest;
import com.relativesHelp.relativesHelp.media.dto.MediaUploadRequest;
import com.relativesHelp.relativesHelp.media.service.CloudinaryExplorerService;
import com.relativesHelp.relativesHelp.media.service.MediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;
    private final CloudinaryExplorerService cloudinaryExplorerService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MediaResponse>> uploadMedia(@RequestPart("file") MultipartFile file,
                                                                  @RequestPart("metadata") @Valid MediaUploadRequest request,
                                                                  Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        MediaResponse response = mediaService.uploadMedia(request, file, userId);
        return ResponseEntity.ok(ApiResponse.success("Uploaded successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MediaResponse>>> listMedia(@RequestParam(required = false) Long familyTreeId,
                                                                      @RequestParam(required = false) Long personId,
                                                                      @RequestParam(required = false) String albumId,
                                                                      @RequestParam(defaultValue = "false") boolean onlyPublic) {
        List<MediaFile> mediaFiles = mediaService.listMedia(familyTreeId, personId, albumId, onlyPublic);
        List<MediaResponse> responses = mediaFiles.stream()
                .map(MediaResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MediaResponse>> getMedia(@PathVariable String id) {
        MediaFile mediaFile = mediaService.getMediaById(id);
        return ResponseEntity.ok(ApiResponse.success(MediaResponse.from(mediaFile)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MediaResponse>> updateMedia(@PathVariable String id,
                                                                  @RequestBody @Valid MediaUpdateRequest request,
                                                                  Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        MediaResponse response = mediaService.updateMedia(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMedia(@PathVariable String id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }

    @GetMapping("/cloudinary/folders")
    public ResponseEntity<ApiResponse<List<CloudinaryFolderDto>>> listFolders(@RequestParam(required = false) String path) {
        List<CloudinaryFolderDto> folders = path == null
                ? cloudinaryExplorerService.listRootFolders()
                : cloudinaryExplorerService.listSubFolders(path);
        return ResponseEntity.ok(ApiResponse.success(folders));
    }

    @GetMapping("/cloudinary/resources")
    public ResponseEntity<ApiResponse<List<CloudinaryResourceDto>>> listResources(@RequestParam(required = false) String folder,
                                                                                  @RequestParam(required = false, defaultValue = "50") Integer limit,
                                                                                  @RequestParam(required = false) String cursor) {
        List<CloudinaryResourceDto> resources = cloudinaryExplorerService.listResources(folder, limit, cursor);
        return ResponseEntity.ok(ApiResponse.success(resources));
    }
}

