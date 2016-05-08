package org.crowdware.repository;

import org.crowdware.domain.Payment;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Payment entity.
 */
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    @Query("select payment from Payment payment where payment.user.login = ?#{principal.username}")
    List<Payment> findByUserIsCurrentUser();

}
