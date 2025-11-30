package com.relativesHelp.relativesHelp.graphql.resolver;

import com.relativesHelp.relativesHelp.event.entity.Event;
import com.relativesHelp.relativesHelp.event.entity.EventParticipant;
import com.relativesHelp.relativesHelp.event.entity.EventType;
import com.relativesHelp.relativesHelp.event.repository.EventParticipantRepository;
import com.relativesHelp.relativesHelp.event.repository.EventRepository;
import com.relativesHelp.relativesHelp.event.repository.EventTypeRepository;
import com.relativesHelp.relativesHelp.event.service.EventService;
import com.relativesHelp.relativesHelp.search.document.EventSearchDocument;
import com.relativesHelp.relativesHelp.search.service.SearchQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventResolver {
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final SearchQueryService searchQueryService;

    @QueryMapping
    public List<Event> events(@Argument Long familyTreeId) {
        return eventRepository.findByFamilyTreeId(familyTreeId);
    }

    @QueryMapping
    public List<Event> upcomingEvents(
            @Argument Long familyTreeId,
            @Argument Integer days) {
        int daysToCheck = days != null ? days : 30;
        return eventService.getUpcomingEvents(familyTreeId, daysToCheck);
    }

    @QueryMapping
    public Event event(@Argument Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @QueryMapping
    public List<Event> searchEvents(
            @Argument Long familyTreeId,
            @Argument String keyword,
            @Argument String fromDate,
            @Argument String toDate) {
        LocalDate from = fromDate != null ? LocalDate.parse(fromDate) : null;
        LocalDate to = toDate != null ? LocalDate.parse(toDate) : null;
        return searchQueryService.searchEvents(
                        familyTreeId,
                        keyword,
                        from,
                        to,
                        PageRequest.of(0, 50))
                .stream()
                .map(EventSearchDocument::toEventEntity)
                .toList();
    }

    @MutationMapping
    public Event createEvent(
            @Argument("input") CreateEventInput input,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        Event event = Event.builder()
                .familyTreeId(input.familyTreeId())
                .personId(input.personId())
                .eventTypeId(input.eventTypeId())
                .title(input.title())
                .description(input.description())
                .eventDate(LocalDate.parse(input.eventDate()))
                .eventTime(input.eventTime() != null ? 
                    LocalTime.parse(input.eventTime()) : null)
                .isRecurring(input.isRecurring() != null ? input.isRecurring() : false)
                .recurrenceRule(input.recurrenceRule())
                .location(input.location())
                .isLunarCalendar(input.isLunarCalendar() != null ? 
                    input.isLunarCalendar() : false)
                .reminderDays(input.reminderDays() != null ? input.reminderDays() : 7)
                .createdByUserId(userId)
                .build();

        return eventService.createEvent(event);
    }

    @MutationMapping
    public EventParticipant addEventParticipant(
            @Argument Long eventId,
            @Argument Long personId) {
        EventParticipant participant = EventParticipant.builder()
                .eventId(eventId)
                .personId(personId)
                .rsvpStatus(EventParticipant.RsvpStatus.PENDING)
                .build();
        return eventParticipantRepository.save(participant);
    }

    @MutationMapping
    public Event updateEvent(
            @Argument Long id,
            @Argument("input") UpdateEventInput input,
            Authentication authentication) {
        Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
        return eventService.updateEvent(id, event -> {
            if (input.title() != null) event.setTitle(input.title());
            if (input.description() != null) event.setDescription(input.description());
            if (input.eventDate() != null) event.setEventDate(LocalDate.parse(input.eventDate()));
            if (input.eventTime() != null) event.setEventTime(LocalTime.parse(input.eventTime()));
            if (input.location() != null) event.setLocation(input.location());
            if (input.reminderDays() != null) event.setReminderDays(input.reminderDays());
        }, userId);
    }

    @MutationMapping
    public Boolean deleteEvent(@Argument Long id, Authentication authentication) {
        Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
        eventService.deleteEvent(id, userId);
        return true;
    }

    @SchemaMapping(typeName = "Event", field = "eventType")
    public EventType getEventType(Event event) {
        return eventTypeRepository.findById(event.getEventTypeId())
                .orElse(null);
    }

    @SchemaMapping(typeName = "Event", field = "participants")
    public List<EventParticipant> getParticipants(Event event) {
        return eventParticipantRepository.findByEventId(event.getId());
    }

    // Input Records
    public record CreateEventInput(
            Long familyTreeId,
            Long personId,
            Long eventTypeId,
            String title,
            String description,
            String eventDate,
            String eventTime,
            Boolean isRecurring,
            String recurrenceRule,
            String location,
            Boolean isLunarCalendar,
            Integer reminderDays
    ) {}

    public record UpdateEventInput(
            String title,
            String description,
            String eventDate,
            String eventTime,
            String location,
            Integer reminderDays
    ) {}
}

