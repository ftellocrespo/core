package ec.com.golem.service;

import ec.com.golem.domain.OrganizationUser;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link ec.com.golem.domain.OrganizationUser}.
 */
public interface OrganizationUserService {
    /**
     * Save a organizationUser.
     *
     * @param organizationUser the entity to save.
     * @return the persisted entity.
     */
    OrganizationUser save(OrganizationUser organizationUser);

    /**
     * Updates a organizationUser.
     *
     * @param organizationUser the entity to update.
     * @return the persisted entity.
     */
    OrganizationUser update(OrganizationUser organizationUser);

    /**
     * Partially updates a organizationUser.
     *
     * @param organizationUser the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OrganizationUser> partialUpdate(OrganizationUser organizationUser);

    /**
     * Get all the organizationUsers.
     *
     * @return the list of entities.
     */
    List<OrganizationUser> findAll();

    /**
     * Get the "id" organizationUser.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OrganizationUser> findOne(Long id);

    /**
     * Delete the "id" organizationUser.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the organizationUser corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<OrganizationUser> search(String query);
}
