package ec.com.golem.service;

import ec.com.golem.domain.ActionGeneric;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link ec.com.golem.domain.ActionGeneric}.
 */
public interface ActionGenericService {
    /**
     * Save a actionGeneric.
     *
     * @param actionGeneric the entity to save.
     * @return the persisted entity.
     */
    ActionGeneric save(ActionGeneric actionGeneric);

    /**
     * Updates a actionGeneric.
     *
     * @param actionGeneric the entity to update.
     * @return the persisted entity.
     */
    ActionGeneric update(ActionGeneric actionGeneric);

    /**
     * Partially updates a actionGeneric.
     *
     * @param actionGeneric the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ActionGeneric> partialUpdate(ActionGeneric actionGeneric);

    /**
     * Get all the actionGenerics.
     *
     * @return the list of entities.
     */
    List<ActionGeneric> findAll();

    /**
     * Get the "id" actionGeneric.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ActionGeneric> findOne(Long id);

    /**
     * Delete the "id" actionGeneric.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the actionGeneric corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<ActionGeneric> search(String query);
}
