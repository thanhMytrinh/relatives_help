package com.relativesHelp.relativesHelp.event.service;

import com.relativesHelp.relativesHelp.event.entity.Event;
import com.relativesHelp.relativesHelp.event.entity.NotificationSchedule;
import com.relativesHelp.relativesHelp.event.repository.EventRepository;
import com.relativesHelp.relativesHelp.event.repository.NotificationScheduleRepository;
import com.relativesHelp.relativesHelp.kafka.event.EventCreatedEvent;
import com.relativesHelp.relativesHelp.kafka.event.EventDeletedEvent;
import com.relativesHelp.relativesHelp.kafka.event.EventUpdatedEvent;
import com.relativesHelp.relativesHelp.kafka.producer.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final NotificationScheduleRepository notificationScheduleRepository;
    private final KafkaEventProducer kafkaEventProducer;

    @Transactional
    public Event createEvent(Event event) {
        Event savedEvent = eventRepository.save(event);

        // Publish event to Kafka asynchronously (non-blocking)
        EventCreatedEvent eventCreatedEvent = EventCreatedEvent.builder()
                .eventId(savedEvent.getId())
                .familyTreeId(savedEvent.getFamilyTreeId())
                .personId(savedEvent.getPersonId())
                .eventType("EVENT") // You can get from EventType
                .title(savedEvent.getTitle())
                .eventDate(savedEvent.getEventDate())
                .isRecurring(savedEvent.getIsRecurring())
                .reminderDays(savedEvent.getReminderDays())
                .createdByUserId(savedEvent.getCreatedByUserId())
                .createdAt(LocalDateTime.now())
                .build();
        kafkaEventProducer.publishEventCreated(eventCreatedEvent);

        log.info("Event created and event published: eventId={}", savedEvent.getId());
        return savedEvent;
    }

    public List<Event> getUpcomingEvents(Long familyTreeId, int days) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);
        return eventRepository.findUpcomingEvents(familyTreeId, startDate, endDate);
    }

    @Transactional
    public Event updateEvent(Long eventId, java.util.function.Consumer<Event> updater, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        updater.accept(event);
        Event saved = eventRepository.save(event);

        EventUpdatedEvent updatedEvent = EventUpdatedEvent.builder()
                .eventId(saved.getId())
                .familyTreeId(saved.getFamilyTreeId())
                .updatedByUserId(userId)
                .updatedAt(LocalDateTime.now())
                .build();
        kafkaEventProducer.publishEventUpdated(updatedEvent);
        log.info("Event updated and event published: {}", saved.getId());
        return saved;
    }

    @Transactional
    public void deleteEvent(Long eventId, Long userId) {
        eventRepository.findById(eventId).ifPresent(event -> {
            eventRepository.delete(event);
            EventDeletedEvent deletedEvent = EventDeletedEvent.builder()
                    .eventId(event.getId())
                    .familyTreeId(event.getFamilyTreeId())
                    .deletedByUserId(userId)
                    .deletedAt(LocalDateTime.now())
                    .build();
            kafkaEventProducer.publishEventDeleted(deletedEvent);
            log.info("Event deleted and event published: {}", eventId);
        });
    }

    // Scheduled job to check for due reminders and publish to Kafka
    @Scheduled(fixedRate = 60000) // Run every minute
    public void checkAndPublishReminders() {
        List<NotificationSchedule> pendingNotifications = 
            notificationScheduleRepository.findPendingNotifications(LocalDateTime.now());

        for (NotificationSchedule schedule : pendingNotifications) {
            // Get event details
            Event event = eventRepository.findById(schedule.getEventId())
                    .orElse(null);
            
            if (event != null) {
                // Publish reminder event to Kafka (non-blocking)
                com.relativesHelp.relativesHelp.kafka.event.EventReminderDueEvent reminderEvent =
                    com.relativesHelp.relativesHelp.kafka.event.EventReminderDueEvent.builder()
                        .eventId(event.getId())
                        .notificationScheduleId(schedule.getId())
                        .userId(schedule.getUserId())
                        .eventTitle(event.getTitle())
                        .eventDate(event.getEventDate())
                        .notificationType(schedule.getNotificationType().name())
                        .reminderTime(schedule.getNotificationDate())
                        .build();
                
                kafkaEventProducer.publishEventReminderDue(reminderEvent);
                log.info("Published reminder event: notificationScheduleId={}", schedule.getId());
            }
        }
    }
}

