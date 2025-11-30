package com.relativesHelp.relativesHelp.notification;

import com.relativesHelp.relativesHelp.kafka.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    // This service processes notifications asynchronously via Kafka events

    public void sendWelcomeNotification(UserRegisteredEvent event) {
        log.info("Sending welcome notification to user: userId={}, email={}", 
            event.getUserId(), event.getEmail());
        // Implement email/push notification logic here
        // This runs asynchronously, so it doesn't block user registration
    }

    public void notifyPersonCreated(PersonCreatedEvent event) {
        log.info("Notifying family members about new person: personId={}, familyTreeId={}", 
            event.getPersonId(), event.getFamilyTreeId());
        // Get family tree members and send notifications
        // This runs asynchronously via Kafka
    }

    public void notifyRelationshipCreated(RelationshipCreatedEvent event) {
        log.info("Notifying about new relationship: relationshipId={}, type={}", 
            event.getRelationshipId(), event.getRelationshipType());
        // Notify relevant family members
    }

    public void scheduleEventReminders(EventCreatedEvent event) {
        log.info("Scheduling reminders for event: eventId={}, reminderDays={}", 
            event.getEventId(), event.getReminderDays());
        // Create notification schedules in database
        // This runs asynchronously, so event creation is not blocked
    }

    public void sendEventReminder(EventReminderDueEvent event) {
        log.info("Sending event reminder: eventId={}, userId={}, type={}", 
            event.getEventId(), event.getUserId(), event.getNotificationType());
        // Send email/push/SMS notification
        // Update notification_schedule.is_sent = true
    }

    public void notifyMediaUploaded(MediaUploadedEvent event) {
        log.info("Notifying about media upload: mediaId={}, familyTreeId={}", 
            event.getMediaId(), event.getFamilyTreeId());
        // Notify family members about new media
    }
}

