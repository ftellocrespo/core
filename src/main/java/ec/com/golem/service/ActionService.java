package ec.com.golem.service;

import ec.com.golem.domain.Action;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link ec.com.golem.domain.Action}.
 */
public interface ActionService {
    /**
     * Save a action.
     *
     * @param action the entity to save.
     * @return the persisted entity.
     */
    Action save(Action action);

    /**
     * Updates a action.
     *
     * @param action the entity to update.
     * @return the persisted entity.
     */
    Action update(Action action);

    /**
     * Partially updates a action.
     *
     * @param action the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Action> partialUpdate(Action action);

    /**
     * Get all the actions.
     *
     * @return the list of entities.
     */
    List<Action> findAll();

    /**
     * Get the "id" action.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Action> findOne(Long id);

    /**
     * Delete the "id" action.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the action corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<Action> search(String query);
}
