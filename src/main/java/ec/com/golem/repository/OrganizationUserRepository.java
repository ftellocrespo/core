package ec.com.golem.repository;

import ec.com.golem.domain.OrganizationUser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrganizationUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, Long> {}
