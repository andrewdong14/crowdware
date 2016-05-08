package org.crowdware.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.crowdware.domain.App;
import org.crowdware.service.AppService;
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
 * REST controller for managing App.
 */
@RestController
@RequestMapping("/api")
public class AppResource {

    private final Logger log = LoggerFactory.getLogger(AppResource.class);
        
    @Inject
    private AppService appService;
    
    /**
     * POST  /apps : Create a new app.
     *
     * @param app the app to create
     * @return the ResponseEntity with status 201 (Created) and with body the new app, or with status 400 (Bad Request) if the app has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/apps",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<App> createApp(@Valid @RequestBody App app) throws URISyntaxException {
        log.debug("REST request to save App : {}", app);
        if (app.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("app", "idexists", "A new app cannot already have an ID")).body(null);
        }
        App result = appService.save(app);
        return ResponseEntity.created(new URI("/api/apps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("app", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /apps : Updates an existing app.
     *
     * @param app the app to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated app,
     * or with status 400 (Bad Request) if the app is not valid,
     * or with status 500 (Internal Server Error) if the app couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/apps",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<App> updateApp(@Valid @RequestBody App app) throws URISyntaxException {
        log.debug("REST request to update App : {}", app);
        if (app.getId() == null) {
            return createApp(app);
        }
        App result = appService.save(app);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("app", app.getId().toString()))
            .body(result);
    }

    /**
     * GET  /apps : get all the apps.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of apps in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/apps",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<App>> getAllApps(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Apps");
        Page<App> page = appService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/apps");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /apps/:id : get the "id" app.
     *
     * @param id the id of the app to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the app, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/apps/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<App> getApp(@PathVariable Long id) {
        log.debug("REST request to get App : {}", id);
        App app = appService.findOne(id);
        return Optional.ofNullable(app)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /apps/:id : delete the "id" app.
     *
     * @param id the id of the app to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/apps/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteApp(@PathVariable Long id) {
        log.debug("REST request to delete App : {}", id);
        appService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("app", id.toString())).build();
    }

    /**
     * SEARCH  /_search/apps?query=:query : search for the app corresponding
     * to the query.
     *
     * @param query the query of the app search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/apps",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<App>> searchApps(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Apps for query {}", query);
        Page<App> page = appService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/apps");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
