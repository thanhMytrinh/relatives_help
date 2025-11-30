package com.relativesHelp.relativesHelp.search.repository;

import com.relativesHelp.relativesHelp.search.document.EventSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventSearchRepository extends ElasticsearchRepository<EventSearchDocument, Long> {
}


