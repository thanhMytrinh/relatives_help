package com.relativesHelp.relativesHelp.media.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.relativesHelp.relativesHelp.media.dto.CloudinaryFolderDto;
import com.relativesHelp.relativesHelp.media.dto.CloudinaryResourceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryExplorerService {
    private final Cloudinary cloudinary;

    public List<CloudinaryFolderDto> listRootFolders() {
        try {
            ApiResponse response = cloudinary.api().rootFolders(Map.of());
            List<Map<String, Object>> folders = (List<Map<String, Object>>) response.get("folders");
            return folders.stream()
                    .map(folder -> CloudinaryFolderDto.builder()
                            .name((String) folder.get("name"))
                            .path((String) folder.get("path"))
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Unable to list root folders", e);
            throw new RuntimeException("Unable to list Cloudinary root folders", e);
        }
    }

    public List<CloudinaryFolderDto> listSubFolders(String path) {
        try {
            ApiResponse response = cloudinary.api().subFolders(path, Map.of());
            List<Map<String, Object>> folders = (List<Map<String, Object>>) response.get("folders");
            return folders.stream()
                    .map(folder -> CloudinaryFolderDto.builder()
                            .name((String) folder.get("name"))
                            .path((String) folder.get("path"))
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Unable to list subfolders for {}", path, e);
            throw new RuntimeException("Unable to list Cloudinary subfolders", e);
        }
    }

    public List<CloudinaryResourceDto> listResources(String folder, Integer maxResults, String nextCursor) {
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("type", "upload");
            options.put("prefix", folder != null ? folder + "/" : "");
            if (maxResults != null) {
                options.put("max_results", maxResults);
            }
            if (nextCursor != null) {
                options.put("next_cursor", nextCursor);
            }

            ApiResponse response = cloudinary.api().resources(options);
            List<Map<String, Object>> resources = (List<Map<String, Object>>) response.get("resources");

            return resources.stream()
                    .map(this::mapResource)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Unable to list resources for folder {}", folder, e);
            throw new RuntimeException("Unable to list Cloudinary resources", e);
        }
    }

    private CloudinaryResourceDto mapResource(Map<String, Object> resource) {
        return CloudinaryResourceDto.builder()
                .publicId((String) resource.get("public_id"))
                .assetId((String) resource.get("asset_id"))
                .type((String) resource.get("type"))
                .resourceType((String) resource.get("resource_type"))
                .format((String) resource.get("format"))
                .url((String) resource.get("url"))
                .secureUrl((String) resource.get("secure_url"))
                .bytes(resource.get("bytes") != null ? ((Number) resource.get("bytes")).longValue() : null)
                .width(resource.get("width") != null ? ((Number) resource.get("width")).intValue() : null)
                .height(resource.get("height") != null ? ((Number) resource.get("height")).intValue() : null)
                .folder((String) resource.get("folder"))
                .createdAt((String) resource.get("created_at"))
                .build();
    }
}

