package org.crowdware.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.crowdware.domain.Task;
import org.crowdware.repository.TaskRepository;
import org.crowdware.repository.search.TaskSearchRepository;
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
 * REST controller for managing Task.
 */
@RestController
@RequestMapping("/api")
public class TaskResource {

    private final Logger log = LoggerFactory.getLogger(TaskResource.class);
        
    @Inject
    private TaskRepository taskRepository;
    
    @Inject
    private TaskSearchRepository taskSearchRepository;
    
    /**
     * POST  /tasks : Create a new task.
     *
     * @param task the task to create
     * @return the ResponseEntity with status 201 (Created) and with body the new task, or with status 400 (Bad Request) if the task has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tasks",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) throws URISyntaxException {
        log.debug("REST request to save Task : {}", task);
        if (task.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("task", "idexists", "A new task cannot already have an ID")).body(null);
        }
        Task result = taskRepository.save(task);
        taskSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("task", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tasks : Updates an existing task.
     *
     * @param task the task to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated task,
     * or with status 400 (Bad Request) if the task is not valid,
     * or with status 500 (Internal Server Error) if the task couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/tasks",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Task> updateTask(@Valid @RequestBody Task task) throws URISyntaxException {
        log.debug("REST request to update Task : {}", task);
        if (task.getId() == null) {
            return createTask(task);
        }
        Task result = taskRepository.save(task);
        taskSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("task", task.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tasks : get all the tasks.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of tasks in body
     */
    @RequestMapping(value = "/tasks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Task> getAllTasks() {
        log.debug("REST request to get all Tasks");
        List<Task> tasks = taskRepository.findAll();
        return tasks;
    }

    /**
     * GET  /tasks/:id : get the "id" task.
     *
     * @param id the id of the task to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the task, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/tasks/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        log.debug("REST request to get Task : {}", id);
        Task task = taskRepository.findOne(id);
        return Optional.ofNullable(task)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tasks/:id : delete the "id" task.
     *
     * @param id the id of the task to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/tasks/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.debug("REST request to delete Task : {}", id);
        taskRepository.delete(id);
        taskSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("task", id.toString())).build();
    }

    /**
     * SEARCH  /_search/tasks?query=:query : search for the task corresponding
     * to the query.
     *
     * @param query the query of the task search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/tasks",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Task> searchTasks(@RequestParam String query) {
        log.debug("REST request to search Tasks for query {}", query);
        return StreamSupport
            .stream(taskSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
