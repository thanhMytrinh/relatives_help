package com.relativesHelp.relativesHelp.kafka.consumer;

import com.relativesHelp.relativesHelp.kafka.event.*;
import com.relativesHelp.relativesHelp.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final NotificationService notificationService;

    @KafkaListener(topics = "user.registered", groupId = "notification-service")
    public void handleUserRegistered(UserRegisteredEvent event, Acknowledgment ack) {
        try {
            log.info("Received user.registered event: userId={}", event.getUserId());
            notificationService.sendWelcomeNotification(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing user.registered event: userId={}", 
                event.getUserId(), e);
            // In production, you might want to implement retry logic or dead letter queue
        }
    }

    @KafkaListener(topics = "family.person.created", groupId = "notification-service")
    public void handlePersonCreated(PersonCreatedEvent event, Acknowledgment ack) {
        try {
            log.info("Received family.person.created event: personId={}", event.getPersonId());
            notificationService.notifyPersonCreated(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing family.person.created event: personId={}", 
                event.getPersonId(), e);
        }
    }

    @KafkaListener(topics = "family.relationship.created", groupId = "notification-service")
    public void handleRelationshipCreated(RelationshipCreatedEvent event, Acknowledgment ack) {
        try {
            log.info("Received family.relationship.created event: relationshipId={}", 
                event.getRelationshipId());
            notificationService.notifyRelationshipCreated(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing family.relationship.created event: relationshipId={}", 
                event.getRelationshipId(), e);
        }
    }

    @KafkaListener(topics = "event.created", groupId = "notification-service")
    public void handleEventCreated(EventCreatedEvent event, Acknowledgment ack) {
        try {
            log.info("Received event.created event: eventId={}", event.getEventId());
            notificationService.scheduleEventReminders(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing event.created event: eventId={}", 
                event.getEventId(), e);
        }
    }

    @KafkaListener(topics = "event.reminder.due", groupId = "notification-service")
    public void handleEventReminderDue(EventReminderDueEvent event, Acknowledgment ack) {
        try {
            log.info("Received event.reminder.due event: notificationScheduleId={}", 
                event.getNotificationScheduleId());
            notificationService.sendEventReminder(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing event.reminder.due event: notificationScheduleId={}", 
                event.getNotificationScheduleId(), e);
        }
    }

    @KafkaListener(topics = "media.uploaded", groupId = "notification-service")
    public void handleMediaUploaded(MediaUploadedEvent event, Acknowledgment ack) {
        try {
            log.info("Received media.uploaded event: mediaId={}", event.getMediaId());
            notificationService.notifyMediaUploaded(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing media.uploaded event: mediaId={}", 
                event.getMediaId(), e);
        }
    }
}

