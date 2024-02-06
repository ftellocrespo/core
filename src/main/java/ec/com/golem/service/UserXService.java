package ec.com.golem.service;

import ec.com.golem.domain.UserX;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link ec.com.golem.domain.UserX}.
 */
public interface UserXService {
    /**
     * Save a userX.
     *
     * @param userX the entity to save.
     * @return the persisted entity.
     */
    UserX save(UserX userX);

    /**
     * Updates a userX.
     *
     * @param userX the entity to update.
     * @return the persisted entity.
     */
    UserX update(UserX userX);

    /**
     * Partially updates a userX.
     *
     * @param userX the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserX> partialUpdate(UserX userX);

    /**
     * Get all the userXES.
     *
     * @return the list of entities.
     */
    List<UserX> findAll();

    /**
     * Get the "id" userX.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserX> findOne(Long id);

    /**
     * Delete the "id" userX.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the userX corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<UserX> search(String query);
}
