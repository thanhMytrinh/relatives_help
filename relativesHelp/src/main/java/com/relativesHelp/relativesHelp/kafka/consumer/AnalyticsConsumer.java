package com.relativesHelp.relativesHelp.kafka.consumer;

import com.relativesHelp.relativesHelp.kafka.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsConsumer {
    // This consumer handles analytics events asynchronously

    @KafkaListener(topics = "family.person.created", groupId = "analytics-service")
    public void handlePersonCreated(PersonCreatedEvent event, Acknowledgment ack) {
        try {
            log.info("Analytics: Processing person.created event: personId={}", event.getPersonId());
            // Update statistics, generate reports, etc.
            // This runs asynchronously without blocking the main flow
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error in analytics processing for person.created: personId={}", 
                event.getPersonId(), e);
        }
    }

    @KafkaListener(topics = "family.relationship.created", groupId = "analytics-service")
    public void handleRelationshipCreated(RelationshipCreatedEvent event, Acknowledgment ack) {
        try {
            log.info("Analytics: Processing relationship.created event: relationshipId={}", 
                event.getRelationshipId());
            // Update relationship statistics
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error in analytics processing for relationship.created: relationshipId={}", 
                event.getRelationshipId(), e);
        }
    }

    @KafkaListener(topics = "user.registered", groupId = "analytics-service")
    public void handleUserRegistered(UserRegisteredEvent event, Acknowledgment ack) {
        try {
            log.info("Analytics: Processing user.registered event: userId={}", event.getUserId());
            // Update user registration statistics
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error in analytics processing for user.registered: userId={}", 
                event.getUserId(), e);
        }
    }
}

