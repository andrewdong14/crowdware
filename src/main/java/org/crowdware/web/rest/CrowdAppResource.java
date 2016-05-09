package org.crowdware.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.crowdware.domain.CrowdApp;
import org.crowdware.service.CrowdAppService;
import org.crowdware.web.rest.util.HeaderUtil;
import org.crowdware.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing CrowdApp.
 */
@RestController
@RequestMapping("/api")
public class CrowdAppResource {

    private final Logger log = LoggerFactory.getLogger(CrowdAppResource.class);
        
    @Inject
    private CrowdAppService crowdAppService;
    
    /**
     * POST  /crowd-apps : Create a new crowdApp.
     *
     * @param crowdApp the crowdApp to create
     * @return the ResponseEntity with status 201 (Created) and with body the new crowdApp, or with status 400 (Bad Request) if the crowdApp has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/crowd-apps",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CrowdApp> createCrowdApp(@Valid @RequestBody CrowdApp crowdApp) throws URISyntaxException {
        log.debug("REST request to save CrowdApp : {}", crowdApp);
        if (crowdApp.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("crowdApp", "idexists", "A new crowdApp cannot already have an ID")).body(null);
        }
        CrowdApp result = crowdAppService.save(crowdApp);
        return ResponseEntity.created(new URI("/api/crowd-apps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("crowdApp", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /crowd-apps : Updates an existing crowdApp.
     *
     * @param crowdApp the crowdApp to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated crowdApp,
     * or with status 400 (Bad Request) if the crowdApp is not valid,
     * or with status 500 (Internal Server Error) if the crowdApp couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/crowd-apps",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CrowdApp> updateCrowdApp(@Valid @RequestBody CrowdApp crowdApp) throws URISyntaxException {
        log.debug("REST request to update CrowdApp : {}", crowdApp);
        if (crowdApp.getId() == null) {
            return createCrowdApp(crowdApp);
        }
        CrowdApp result = crowdAppService.save(crowdApp);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("crowdApp", crowdApp.getId().toString()))
            .body(result);
    }

    /**
     * GET  /crowd-apps : get all the crowdApps.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of crowdApps in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/crowd-apps",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CrowdApp>> getAllCrowdApps(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of CrowdApps");
        Page<CrowdApp> page = crowdAppService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/crowd-apps");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /crowd-apps/:id : get the "id" crowdApp.
     *
     * @param id the id of the crowdApp to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the crowdApp, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/crowd-apps/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CrowdApp> getCrowdApp(@PathVariable Long id) {
        log.debug("REST request to get CrowdApp : {}", id);
        CrowdApp crowdApp = crowdAppService.findOne(id);
        return Optional.ofNullable(crowdApp)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /crowd-apps/:id : delete the "id" crowdApp.
     *
     * @param id the id of the crowdApp to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/crowd-apps/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCrowdApp(@PathVariable Long id) {
        log.debug("REST request to delete CrowdApp : {}", id);
        crowdAppService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("crowdApp", id.toString())).build();
    }

    /**
     * SEARCH  /_search/crowd-apps?query=:query : search for the crowdApp corresponding
     * to the query.
     *
     * @param query the query of the crowdApp search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/crowd-apps",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CrowdApp>> searchCrowdApps(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of CrowdApps for query {}", query);
        Page<CrowdApp> page = crowdAppService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/crowd-apps");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
