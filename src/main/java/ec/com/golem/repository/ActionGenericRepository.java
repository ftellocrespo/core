package ec.com.golem.repository;

import ec.com.golem.domain.ActionGeneric;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ActionGeneric entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActionGenericRepository extends JpaRepository<ActionGeneric, Long> {}
