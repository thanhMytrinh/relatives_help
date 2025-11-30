package com.relativesHelp.relativesHelp.search.service;

import com.relativesHelp.relativesHelp.search.document.EventSearchDocument;
import com.relativesHelp.relativesHelp.search.document.PersonSearchDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchIndexInitializer {

    private final ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void createIndexes() {
        createIndexIfNotExists(PersonSearchDocument.class);
        createIndexIfNotExists(EventSearchDocument.class);
    }

    private void createIndexIfNotExists(Class<?> documentClass) {
        IndexOperations indexOps = elasticsearchOperations.indexOps(documentClass);
        if (indexOps.exists()) {
            return;
        }

        log.info("Creating Elasticsearch index for {}", documentClass.getSimpleName());
        indexOps.create();
        Document mapping = indexOps.createMapping(documentClass);
        indexOps.putMapping(mapping);
        indexOps.refresh();
    }
}


