package com.relativesHelp.relativesHelp.search.controller;

import com.relativesHelp.relativesHelp.common.dto.ApiResponse;
import com.relativesHelp.relativesHelp.search.document.EventSearchDocument;
import com.relativesHelp.relativesHelp.search.document.PersonSearchDocument;
import com.relativesHelp.relativesHelp.search.dto.EventSearchResponse;
import com.relativesHelp.relativesHelp.search.dto.PersonSearchResponse;
import com.relativesHelp.relativesHelp.search.service.SearchQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchQueryService searchQueryService;

    @GetMapping("/persons")
    public ApiResponse<Page<PersonSearchResponse>> searchPersons(
            @RequestParam Long familyTreeId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PersonSearchDocument> docs =
                searchQueryService.searchPersons(familyTreeId, keyword, pageable);

        List<PersonSearchResponse> content = docs.getContent().stream()
                .map(doc -> PersonSearchResponse.builder()
                        .id(doc.getId())
                        .familyTreeId(doc.getFamilyTreeId())
                        .userId(doc.getUserId())
                        .fullName(doc.getFullName())
                        .gender(doc.getGender())
                        .dateOfBirth(doc.getDateOfBirth())
                        .dateOfDeath(doc.getDateOfDeath())
                        .placeOfBirth(doc.getPlaceOfBirth())
                        .placeOfDeath(doc.getPlaceOfDeath())
                        .isAlive(doc.getIsAlive())
                        .biography(doc.getBiography())
                        .occupation(doc.getOccupation())
                        .generationLevel(doc.getGenerationLevel())
                        .avatarUrl(doc.getAvatarUrl())
                        .createdAt(doc.getCreatedAt())
                        .updatedAt(doc.getUpdatedAt())
                        .build())
                .toList();

        Page<PersonSearchResponse> responsePage =
                new PageImpl<>(content, pageable, docs.getTotalElements());
        return ApiResponse.success("Search persons successfully", responsePage);
    }

    @GetMapping("/events")
    public ApiResponse<Page<EventSearchResponse>> searchEvents(
            @RequestParam Long familyTreeId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EventSearchDocument> docs =
                searchQueryService.searchEvents(familyTreeId, keyword, fromDate, toDate, pageable);

        List<EventSearchResponse> content = docs.getContent().stream()
                .map(doc -> EventSearchResponse.builder()
                        .id(doc.getId())
                        .familyTreeId(doc.getFamilyTreeId())
                        .personId(doc.getPersonId())
                        .eventTypeId(doc.getEventTypeId())
                        .title(doc.getTitle())
                        .description(doc.getDescription())
                        .eventDate(doc.getEventDate())
                        .eventTime(doc.getEventTime())
                        .isRecurring(doc.getIsRecurring())
                        .recurrenceRule(doc.getRecurrenceRule())
                        .location(doc.getLocation())
                        .isLunarCalendar(doc.getIsLunarCalendar())
                        .reminderDays(doc.getReminderDays())
                        .createdByUserId(doc.getCreatedByUserId())
                        .createdAt(doc.getCreatedAt())
                        .updatedAt(doc.getUpdatedAt())
                        .build())
                .toList();

        Page<EventSearchResponse> responsePage =
                new PageImpl<>(content, pageable, docs.getTotalElements());
        return ApiResponse.success("Search events successfully", responsePage);
    }
}


