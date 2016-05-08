package org.crowdware.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.crowdware.domain.JobAttribute;
import org.crowdware.repository.JobAttributeRepository;
import org.crowdware.repository.search.JobAttributeSearchRepository;
import org.crowdware.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing JobAttribute.
 */
@RestController
@RequestMapping("/api")
public class JobAttributeResource {

    private final Logger log = LoggerFactory.getLogger(JobAttributeResource.class);
        
    @Inject
    private JobAttributeRepository jobAttributeRepository;
    
    @Inject
    private JobAttributeSearchRepository jobAttributeSearchRepository;
    
    /**
     * POST  /job-attributes : Create a new jobAttribute.
     *
     * @param jobAttribute the jobAttribute to create
     * @return the ResponseEntity with status 201 (Created) and with body the new jobAttribute, or with status 400 (Bad Request) if the jobAttribute has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/job-attributes",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JobAttribute> createJobAttribute(@Valid @RequestBody JobAttribute jobAttribute) throws URISyntaxException {
        log.debug("REST request to save JobAttribute : {}", jobAttribute);
        if (jobAttribute.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("jobAttribute", "idexists", "A new jobAttribute cannot already have an ID")).body(null);
        }
        JobAttribute result = jobAttributeRepository.save(jobAttribute);
        jobAttributeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/job-attributes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("jobAttribute", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /job-attributes : Updates an existing jobAttribute.
     *
     * @param jobAttribute the jobAttribute to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated jobAttribute,
     * or with status 400 (Bad Request) if the jobAttribute is not valid,
     * or with status 500 (Internal Server Error) if the jobAttribute couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/job-attributes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JobAttribute> updateJobAttribute(@Valid @RequestBody JobAttribute jobAttribute) throws URISyntaxException {
        log.debug("REST request to update JobAttribute : {}", jobAttribute);
        if (jobAttribute.getId() == null) {
            return createJobAttribute(jobAttribute);
        }
        JobAttribute result = jobAttributeRepository.save(jobAttribute);
        jobAttributeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("jobAttribute", jobAttribute.getId().toString()))
            .body(result);
    }

    /**
     * GET  /job-attributes : get all the jobAttributes.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of jobAttributes in body
     */
    @RequestMapping(value = "/job-attributes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<JobAttribute> getAllJobAttributes() {
        log.debug("REST request to get all JobAttributes");
        List<JobAttribute> jobAttributes = jobAttributeRepository.findAll();
        return jobAttributes;
    }

    /**
     * GET  /job-attributes/:id : get the "id" jobAttribute.
     *
     * @param id the id of the jobAttribute to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the jobAttribute, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/job-attributes/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JobAttribute> getJobAttribute(@PathVariable Long id) {
        log.debug("REST request to get JobAttribute : {}", id);
        JobAttribute jobAttribute = jobAttributeRepository.findOne(id);
        return Optional.ofNullable(jobAttribute)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /job-attributes/:id : delete the "id" jobAttribute.
     *
     * @param id the id of the jobAttribute to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/job-attributes/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteJobAttribute(@PathVariable Long id) {
        log.debug("REST request to delete JobAttribute : {}", id);
        jobAttributeRepository.delete(id);
        jobAttributeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("jobAttribute", id.toString())).build();
    }

    /**
     * SEARCH  /_search/job-attributes?query=:query : search for the jobAttribute corresponding
     * to the query.
     *
     * @param query the query of the jobAttribute search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/job-attributes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<JobAttribute> searchJobAttributes(@RequestParam String query) {
        log.debug("REST request to search JobAttributes for query {}", query);
        return StreamSupport
            .stream(jobAttributeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
