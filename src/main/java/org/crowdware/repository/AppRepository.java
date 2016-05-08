package org.crowdware.repository;

import org.crowdware.domain.App;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the App entity.
 */
public interface AppRepository extends JpaRepository<App,Long> {

    @Query("select app from App app where app.user_app.login = ?#{principal.username}")
    List<App> findByUser_appIsCurrentUser();

}
