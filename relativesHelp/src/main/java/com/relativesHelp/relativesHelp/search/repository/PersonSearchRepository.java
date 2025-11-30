package com.relativesHelp.relativesHelp.search.repository;

import com.relativesHelp.relativesHelp.search.document.PersonSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonSearchRepository extends ElasticsearchRepository<PersonSearchDocument, Long> {
}


