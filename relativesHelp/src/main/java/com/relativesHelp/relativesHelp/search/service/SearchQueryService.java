package com.relativesHelp.relativesHelp.search.service;

import com.relativesHelp.relativesHelp.search.document.EventSearchDocument;
import com.relativesHelp.relativesHelp.search.document.PersonSearchDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SearchQueryService {

    private final ElasticsearchOperations elasticsearchOperations;

    public Page<PersonSearchDocument> searchPersons(Long familyTreeId,
                                                    String keyword,
                                                    Pageable pageable) {
        Criteria criteria = new Criteria("familyTreeId").is(familyTreeId);

        if (StringUtils.hasText(keyword)) {
            Criteria keywordCriteria = new Criteria("fullName").matches(keyword)
                    .or(new Criteria("biography").matches(keyword))
                    .or(new Criteria("occupation").matches(keyword));
            criteria = criteria.and(keywordCriteria);
        }

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setPageable(pageable);

        SearchHits<PersonSearchDocument> hits =
                elasticsearchOperations.search(query, PersonSearchDocument.class);
        return SearchHitSupport.searchPageFor(hits, pageable).map(SearchHit::getContent);
    }

    public Page<EventSearchDocument> searchEvents(Long familyTreeId,
                                                  String keyword,
                                                  LocalDate fromDate,
                                                  LocalDate toDate,
                                                  Pageable pageable) {
        Criteria criteria = new Criteria("familyTreeId").is(familyTreeId);

        if (StringUtils.hasText(keyword)) {
            Criteria keywordCriteria = new Criteria("title").matches(keyword)
                    .or(new Criteria("description").matches(keyword))
                    .or(new Criteria("location").matches(keyword));
            criteria = criteria.and(keywordCriteria);
        }

        if (fromDate != null) {
            criteria = criteria.and(new Criteria("eventDate").greaterThanEqual(fromDate));
        }
        if (toDate != null) {
            criteria = criteria.and(new Criteria("eventDate").lessThanEqual(toDate));
        }

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setPageable(pageable);

        SearchHits<EventSearchDocument> hits =
                elasticsearchOperations.search(query, EventSearchDocument.class);
        return SearchHitSupport.searchPageFor(hits, pageable).map(SearchHit::getContent);
    }
}


