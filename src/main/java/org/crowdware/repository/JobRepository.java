package org.crowdware.repository;

import org.crowdware.domain.Job;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Job entity.
 */
public interface JobRepository extends JpaRepository<Job,Long> {

    @Query("select job from Job job where job.user.login = ?#{principal.username}")
    List<Job> findByUserIsCurrentUser();

}
