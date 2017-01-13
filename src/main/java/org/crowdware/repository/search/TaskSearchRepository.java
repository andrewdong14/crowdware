package org.crowdware.repository.search;

import org.crowdware.domain.Task;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Task entity.
 */
public interface TaskSearchRepository extends ElasticsearchRepository<Task, Long> {
}
