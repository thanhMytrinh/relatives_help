package com.relativesHelp.relativesHelp.search.service;

import com.relativesHelp.relativesHelp.event.entity.Event;
import com.relativesHelp.relativesHelp.event.repository.EventRepository;
import com.relativesHelp.relativesHelp.family.entity.Person;
import com.relativesHelp.relativesHelp.family.repository.PersonRepository;
import com.relativesHelp.relativesHelp.search.document.EventSearchDocument;
import com.relativesHelp.relativesHelp.search.document.PersonSearchDocument;
import com.relativesHelp.relativesHelp.search.repository.EventSearchRepository;
import com.relativesHelp.relativesHelp.search.repository.PersonSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchIndexService {

    private final PersonRepository personRepository;
    private final EventRepository eventRepository;
    private final PersonSearchRepository personSearchRepository;
    private final EventSearchRepository eventSearchRepository;

    @Transactional(readOnly = true)
    public void indexPerson(Long personId) {
        personRepository.findById(personId)
                .ifPresent(person -> {
                    personSearchRepository.save(PersonSearchDocument.fromPerson(person));
                    log.info("Person indexed to Elasticsearch: {}", personId);
                });
    }

    public void removePerson(Long personId) {
        personSearchRepository.deleteById(personId);
        log.info("Person removed from Elasticsearch: {}", personId);
    }

    @Transactional(readOnly = true)
    public void indexEvent(Long eventId) {
        eventRepository.findById(eventId)
                .ifPresent(event -> {
                    eventSearchRepository.save(EventSearchDocument.fromEvent(event));
                    log.info("Event indexed to Elasticsearch: {}", eventId);
                });
    }

    public void removeEvent(Long eventId) {
        eventSearchRepository.deleteById(eventId);
        log.info("Event removed from Elasticsearch: {}", eventId);
    }

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 3 * * *")
    public void reindexAll() {
        log.info("Starting full reindex of persons and events");
        List<Person> persons = personRepository.findAll();
        persons.stream()
                .map(PersonSearchDocument::fromPerson)
                .forEach(personSearchRepository::save);

        List<Event> events = eventRepository.findAll();
        events.stream()
                .map(EventSearchDocument::fromEvent)
                .forEach(eventSearchRepository::save);

        log.info("Full reindex completed: {} persons, {} events", persons.size(), events.size());
    }
}


