package org.crowdware.repository;

import org.crowdware.domain.CrowdApp;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the CrowdApp entity.
 */
public interface CrowdAppRepository extends JpaRepository<CrowdApp,Long> {

    @Query("select crowdApp from CrowdApp crowdApp where crowdApp.user.login = ?#{principal.username}")
    List<CrowdApp> findByUserIsCurrentUser();

}
