package org.crowdware.repository.search;

import org.crowdware.domain.App;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the App entity.
 */
public interface AppSearchRepository extends ElasticsearchRepository<App, Long> {
}
