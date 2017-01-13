package org.crowdware.repository;

import org.crowdware.domain.Task;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Task entity.
 */
public interface TaskRepository extends JpaRepository<Task,Long> {

}
