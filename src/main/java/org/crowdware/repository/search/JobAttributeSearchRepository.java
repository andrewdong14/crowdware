package org.crowdware.repository.search;

import org.crowdware.domain.JobAttribute;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the JobAttribute entity.
 */
public interface JobAttributeSearchRepository extends ElasticsearchRepository<JobAttribute, Long> {
}
