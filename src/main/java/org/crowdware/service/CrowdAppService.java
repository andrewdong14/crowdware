package org.crowdware.service;

import org.crowdware.domain.CrowdApp;
import org.crowdware.repository.CrowdAppRepository;
import org.crowdware.repository.search.CrowdAppSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing CrowdApp.
 */
@Service
@Transactional
public class CrowdAppService {

    private final Logger log = LoggerFactory.getLogger(CrowdAppService.class);
    
    @Inject
    private CrowdAppRepository crowdAppRepository;
    
    @Inject
    private CrowdAppSearchRepository crowdAppSearchRepository;
    
    /**
     * Save a crowdApp.
     * 
     * @param crowdApp the entity to save
     * @return the persisted entity
     */
    public CrowdApp save(CrowdApp crowdApp) {
        log.debug("Request to save CrowdApp : {}", crowdApp);
        CrowdApp result = crowdAppRepository.save(crowdApp);
        crowdAppSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the crowdApps.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<CrowdApp> findAll(Pageable pageable) {
        log.debug("Request to get all CrowdApps");
        Page<CrowdApp> result = crowdAppRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one crowdApp by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public CrowdApp findOne(Long id) {
        log.debug("Request to get CrowdApp : {}", id);
        CrowdApp crowdApp = crowdAppRepository.findOne(id);
        return crowdApp;
    }

    /**
     *  Delete the  crowdApp by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete CrowdApp : {}", id);
        crowdAppRepository.delete(id);
        crowdAppSearchRepository.delete(id);
    }

    /**
     * Search for the crowdApp corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<CrowdApp> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of CrowdApps for query {}", query);
        return crowdAppSearchRepository.search(queryStringQuery(query), pageable);
    }
}
