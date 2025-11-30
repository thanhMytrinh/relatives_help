package com.relativesHelp.relativesHelp.kafka.producer;

import com.relativesHelp.relativesHelp.kafka.event.EventCreatedEvent;
import com.relativesHelp.relativesHelp.kafka.event.EventDeletedEvent;
import com.relativesHelp.relativesHelp.kafka.event.EventReminderDueEvent;
import com.relativesHelp.relativesHelp.kafka.event.EventUpdatedEvent;
import com.relativesHelp.relativesHelp.kafka.event.MediaUploadedEvent;
import com.relativesHelp.relativesHelp.kafka.event.PersonCreatedEvent;
import com.relativesHelp.relativesHelp.kafka.event.PersonDeletedEvent;
import com.relativesHelp.relativesHelp.kafka.event.PersonUpdatedEvent;
import com.relativesHelp.relativesHelp.kafka.event.RelationshipCreatedEvent;
import com.relativesHelp.relativesHelp.kafka.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserRegistered(UserRegisteredEvent event) {
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send("user.registered", event.getUserId().toString(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent user.registered event: userId={}, offset={}", 
                    event.getUserId(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send user.registered event: userId={}", 
                    event.getUserId(), ex);
            }
        });
    }

    public void publishPersonCreated(PersonCreatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send("family.person.created", event.getPersonId().toString(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent family.person.created event: personId={}, offset={}", 
                    event.getPersonId(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send family.person.created event: personId={}", 
                    event.getPersonId(), ex);
            }
        });
    }

    public void publishPersonUpdated(PersonUpdatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send("family.person.updated", event.getPersonId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent family.person.updated event: personId={}, offset={}",
                        event.getPersonId(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send family.person.updated event: personId={}",
                        event.getPersonId(), ex);
            }
        });
    }

    public void publishPersonDeleted(PersonDeletedEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send("family.person.deleted", event.getPersonId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent family.person.deleted event: personId={}, offset={}",
                        event.getPersonId(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send family.person.deleted event: personId={}",
                        event.getPersonId(), ex);
            }
        });
    }

    public void publishRelationshipCreated(RelationshipCreatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send("family.relationship.created", 
                event.getRelationshipId().toString(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent family.relationship.created event: relationshipId={}, offset={}", 
                    event.getRelationshipId(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send family.relationship.created event: relationshipId={}", 
                    event.getRelationshipId(), ex);
            }
        });
    }

    public void publishEventCreated(EventCreatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send("event.created", event.getEventId().toString(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent event.created event: eventId={}, offset={}", 
                    event.getEventId(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send event.created event: eventId={}", 
                    event.getEventId(), ex);
            }
        });
    }

    public void publishMediaUploaded(MediaUploadedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send("media.uploaded", event.getMediaId(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent media.uploaded event: mediaId={}, offset={}", 
                    event.getMediaId(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send media.uploaded event: mediaId={}", 
                    event.getMediaId(), ex);
            }
        });
    }

    public void publishEventReminderDue(EventReminderDueEvent event) {
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send("event.reminder.due", 
                event.getNotificationScheduleId().toString(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent event.reminder.due event: notificationScheduleId={}, offset={}", 
                    event.getNotificationScheduleId(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send event.reminder.due event: notificationScheduleId={}", 
                    event.getNotificationScheduleId(), ex);
            }
        });
    }

    public void publishEventUpdated(EventUpdatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send("event.updated", event.getEventId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent event.updated event: eventId={}, offset={}",
                        event.getEventId(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send event.updated event: eventId={}",
                        event.getEventId(), ex);
            }
        });
    }

    public void publishEventDeleted(EventDeletedEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send("event.deleted", event.getEventId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent event.deleted event: eventId={}, offset={}",
                        event.getEventId(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send event.deleted event: eventId={}",
                        event.getEventId(), ex);
            }
        });
    }
}

