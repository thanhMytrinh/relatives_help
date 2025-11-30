package com.relativesHelp.relativesHelp.kafka.consumer;

import com.relativesHelp.relativesHelp.kafka.event.EventCreatedEvent;
import com.relativesHelp.relativesHelp.kafka.event.EventDeletedEvent;
import com.relativesHelp.relativesHelp.kafka.event.EventUpdatedEvent;
import com.relativesHelp.relativesHelp.kafka.event.PersonCreatedEvent;
import com.relativesHelp.relativesHelp.kafka.event.PersonDeletedEvent;
import com.relativesHelp.relativesHelp.kafka.event.PersonUpdatedEvent;
import com.relativesHelp.relativesHelp.search.service.SearchIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchIndexConsumer {

    private final SearchIndexService searchIndexService;

    @KafkaListener(topics = "family.person.created", groupId = "search-indexer", concurrency = "3")
    public void handlePersonCreated(PersonCreatedEvent event, Acknowledgment ack) {
        try {
            searchIndexService.indexPerson(event.getPersonId());
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to index person from event {}", event.getPersonId(), ex);
        }
    }

    @KafkaListener(topics = "family.person.updated", groupId = "search-indexer", concurrency = "3")
    public void handlePersonUpdated(PersonUpdatedEvent event, Acknowledgment ack) {
        try {
            searchIndexService.indexPerson(event.getPersonId());
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to reindex person from event {}", event.getPersonId(), ex);
        }
    }

    @KafkaListener(topics = "family.person.deleted", groupId = "search-indexer", concurrency = "3")
    public void handlePersonDeleted(PersonDeletedEvent event, Acknowledgment ack) {
        try {
            searchIndexService.removePerson(event.getPersonId());
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to remove person from index {}", event.getPersonId(), ex);
        }
    }

    @KafkaListener(topics = "event.created", groupId = "search-indexer", concurrency = "3")
    public void handleEventCreated(EventCreatedEvent event, Acknowledgment ack) {
        try {
            searchIndexService.indexEvent(event.getEventId());
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to index event from event {}", event.getEventId(), ex);
        }
    }

    @KafkaListener(topics = "event.updated", groupId = "search-indexer", concurrency = "3")
    public void handleEventUpdated(EventUpdatedEvent event, Acknowledgment ack) {
        try {
            searchIndexService.indexEvent(event.getEventId());
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to reindex event {}", event.getEventId(), ex);
        }
    }

    @KafkaListener(topics = "event.deleted", groupId = "search-indexer", concurrency = "3")
    public void handleEventDeleted(EventDeletedEvent event, Acknowledgment ack) {
        try {
            searchIndexService.removeEvent(event.getEventId());
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to remove event {}", event.getEventId(), ex);
        }
    }
}


