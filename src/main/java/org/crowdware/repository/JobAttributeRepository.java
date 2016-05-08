package org.crowdware.repository;

import org.crowdware.domain.JobAttribute;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the JobAttribute entity.
 */
public interface JobAttributeRepository extends JpaRepository<JobAttribute,Long> {

}
