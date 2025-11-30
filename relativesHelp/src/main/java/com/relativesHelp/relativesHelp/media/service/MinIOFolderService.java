package com.relativesHelp.relativesHelp.media.service;

import com.relativesHelp.relativesHelp.media.dto.MinIOFolderInfo;
import com.relativesHelp.relativesHelp.media.repository.MediaFileRepository;
import com.relativesHelp.relativesHelp.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinIOFolderService {

    private final MinIOService minIOService;
    private final MediaRepository mediaRepository;
    private final MediaFileRepository mediaFileRepository;

    /**
     * Get all folders for a user
     * @param userId User ID
     * @return List of folder info
     */
    public List<MinIOFolderInfo> getUserFolders(Long userId) {
        List<MinIOFolderInfo> folders = new ArrayList<>();

        // 1. User's personal folder
        String userFolderPath = "users/" + userId;
        MinIOFolderInfo userFolder = buildFolderInfo(userFolderPath, "user", userId);
        if (userFolder.getFileCount() > 0 || !minIOService.listFolders(userFolderPath).isEmpty()) {
            folders.add(userFolder);
        }

        // 2. Get all families where user has uploaded media
        List<Long> familyIds = mediaRepository.findByUploadedById(userId).stream()
                .map(media -> media.getFamily().getId())
                .distinct()
                .collect(Collectors.toList());

        for (Long familyId : familyIds) {
            String familyFolderPath = "family/" + familyId;
            MinIOFolderInfo familyFolder = buildFolderInfo(familyFolderPath, "family", familyId);
            if (familyFolder.getFileCount() > 0 || !minIOService.listFolders(familyFolderPath).isEmpty()) {
                folders.add(familyFolder);
            }
        }

        // 3. Get all family trees from MediaFile (MongoDB)
        List<Long> familyTreeIds = mediaFileRepository.findAll().stream()
                .filter(mf -> mf.getUploadedByUserId() != null && mf.getUploadedByUserId().equals(userId))
                .map(mf -> mf.getFamilyTreeId())
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        for (Long familyTreeId : familyTreeIds) {
            String familyTreeFolderPath = "family/" + familyTreeId;
            MinIOFolderInfo familyTreeFolder = buildFolderInfo(familyTreeFolderPath, "family", familyTreeId);
            if (familyTreeFolder.getFileCount() > 0 || !minIOService.listFolders(familyTreeFolderPath).isEmpty()) {
                // Check if already added
                boolean exists = folders.stream()
                        .anyMatch(f -> f.getEntityType().equals("family") && f.getEntityId().equals(familyTreeId));
                if (!exists) {
                    folders.add(familyTreeFolder);
                }
            }
        }

        return folders;
    }

    /**
     * Get folders for a family
     * @param familyId Family ID
     * @return List of folder info
     */
    public List<MinIOFolderInfo> getFamilyFolders(Long familyId) {
        List<MinIOFolderInfo> folders = new ArrayList<>();
        String familyFolderPath = "family/" + familyId;

        // Main family folder
        MinIOFolderInfo familyFolder = buildFolderInfo(familyFolderPath, "family", familyId);
        folders.add(familyFolder);

        // Get subfolders (person, album)
        List<String> subFolders = minIOService.listFolders(familyFolderPath);
        for (String subFolder : subFolders) {
            String relativePath = subFolder.replace(familyFolderPath + "/", "");
            if (relativePath.startsWith("person/")) {
                String personIdStr = relativePath.replace("person/", "").split("/")[0];
                try {
                    Long personId = Long.parseLong(personIdStr);
                    MinIOFolderInfo personFolder = buildFolderInfo(subFolder, "person", personId);
                    folders.add(personFolder);
                } catch (NumberFormatException e) {
                    log.warn("Invalid person ID in folder path: {}", subFolder);
                }
            } else if (relativePath.startsWith("album/")) {
                String albumId = relativePath.replace("album/", "").split("/")[0];
                MinIOFolderInfo albumFolder = buildFolderInfo(subFolder, "album", null);
                folders.add(albumFolder);
            }
        }

        return folders;
    }

    /**
     * Get folders for a person
     * @param personId Person ID
     * @param familyId Family ID (to build path)
     * @return List of folder info
     */
    public List<MinIOFolderInfo> getPersonFolders(Long personId, Long familyId) {
        List<MinIOFolderInfo> folders = new ArrayList<>();
        String personFolderPath = "family/" + familyId + "/person/" + personId;

        MinIOFolderInfo personFolder = buildFolderInfo(personFolderPath, "person", personId);
        folders.add(personFolder);

        return folders;
    }

    /**
     * Get folders for an album
     * @param albumId Album ID
     * @param familyId Family ID (to build path)
     * @return List of folder info
     */
    public List<MinIOFolderInfo> getAlbumFolders(String albumId, Long familyId) {
        List<MinIOFolderInfo> folders = new ArrayList<>();
        String albumFolderPath = "family/" + familyId + "/album/" + albumId;

        MinIOFolderInfo albumFolder = buildFolderInfo(albumFolderPath, "album", null);
        folders.add(albumFolder);

        return folders;
    }

    /**
     * Get all folders for entities related to media
     * @return List of all folder info
     */
    public List<MinIOFolderInfo> getAllEntityFolders() {
        List<MinIOFolderInfo> folders = new ArrayList<>();
        Set<String> processedPaths = new HashSet<>();

        // Get all folders from MinIO
        List<String> allFolders = minIOService.listFolders("");

        for (String folderPath : allFolders) {
            if (processedPaths.contains(folderPath)) {
                continue;
            }

            MinIOFolderInfo folderInfo = null;
            if (folderPath.startsWith("users/")) {
                String userIdStr = folderPath.replace("users/", "").split("/")[0];
                try {
                    Long userId = Long.parseLong(userIdStr);
                    folderInfo = buildFolderInfo(folderPath, "user", userId);
                } catch (NumberFormatException e) {
                    log.warn("Invalid user ID in folder path: {}", folderPath);
                }
            } else if (folderPath.startsWith("family/")) {
                String[] parts = folderPath.replace("family/", "").split("/");
                if (parts.length > 0) {
                    try {
                        Long familyId = Long.parseLong(parts[0]);
                        if (parts.length > 1 && parts[1].equals("person")) {
                            if (parts.length > 2) {
                                Long personId = Long.parseLong(parts[2]);
                                folderInfo = buildFolderInfo(folderPath, "person", personId);
                            }
                        } else if (parts.length > 1 && parts[1].equals("album")) {
                            String albumId = parts.length > 2 ? parts[2] : null;
                            folderInfo = buildFolderInfo(folderPath, "album", null);
                        } else {
                            folderInfo = buildFolderInfo(folderPath, "family", familyId);
                        }
                    } catch (NumberFormatException e) {
                        log.warn("Invalid ID in folder path: {}", folderPath);
                    }
                }
            }

            if (folderInfo != null) {
                folders.add(folderInfo);
                processedPaths.add(folderPath);
            }
        }

        return folders;
    }

    /**
     * Build folder info from path
     */
    private MinIOFolderInfo buildFolderInfo(String folderPath, String entityType, Long entityId) {
        List<String> files = minIOService.listFiles(folderPath);
        List<String> subFolders = minIOService.listFolders(folderPath);
        
        String folderName = folderPath.substring(folderPath.lastIndexOf("/") + 1);
        if (folderName.isEmpty()) {
            folderName = folderPath;
        }

        return MinIOFolderInfo.builder()
                .folderPath(folderPath)
                .folderName(folderName)
                .fileCount(files.size())
                .subFolders(subFolders)
                .files(files)
                .entityType(entityType)
                .entityId(entityId)
                .build();
    }
}

