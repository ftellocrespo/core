package ec.com.golem.repository;

import ec.com.golem.domain.UserX;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserX entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserXRepository extends JpaRepository<UserX, Long> {}
