package org.crowdware.repository.search;

import org.crowdware.domain.CrowdApp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the CrowdApp entity.
 */
public interface CrowdAppSearchRepository extends ElasticsearchRepository<CrowdApp, Long> {
}
