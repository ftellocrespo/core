package ec.com.golem.service.impl;

import ec.com.golem.domain.OrganizationUser;
import ec.com.golem.repository.OrganizationUserRepository;
import ec.com.golem.repository.search.OrganizationUserSearchRepository;
import ec.com.golem.service.OrganizationUserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ec.com.golem.domain.OrganizationUser}.
 */
@Service
@Transactional
public class OrganizationUserServiceImpl implements OrganizationUserService {

    private final Logger log = LoggerFactory.getLogger(OrganizationUserServiceImpl.class);

    private final OrganizationUserRepository organizationUserRepository;

    private final OrganizationUserSearchRepository organizationUserSearchRepository;

    public OrganizationUserServiceImpl(
        OrganizationUserRepository organizationUserRepository,
        OrganizationUserSearchRepository organizationUserSearchRepository
    ) {
        this.organizationUserRepository = organizationUserRepository;
        this.organizationUserSearchRepository = organizationUserSearchRepository;
    }

    @Override
    public OrganizationUser save(OrganizationUser organizationUser) {
        log.debug("Request to save OrganizationUser : {}", organizationUser);
        OrganizationUser result = organizationUserRepository.save(organizationUser);
        organizationUserSearchRepository.index(result);
        return result;
    }

    @Override
    public OrganizationUser update(OrganizationUser organizationUser) {
        log.debug("Request to update OrganizationUser : {}", organizationUser);
        OrganizationUser result = organizationUserRepository.save(organizationUser);
        organizationUserSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<OrganizationUser> partialUpdate(OrganizationUser organizationUser) {
        log.debug("Request to partially update OrganizationUser : {}", organizationUser);

        return organizationUserRepository
            .findById(organizationUser.getId())
            .map(existingOrganizationUser -> {
                if (organizationUser.getState() != null) {
                    existingOrganizationUser.setState(organizationUser.getState());
                }
                if (organizationUser.getRole() != null) {
                    existingOrganizationUser.setRole(organizationUser.getRole());
                }

                return existingOrganizationUser;
            })
            .map(organizationUserRepository::save)
            .map(savedOrganizationUser -> {
                organizationUserSearchRepository.index(savedOrganizationUser);
                return savedOrganizationUser;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationUser> findAll() {
        log.debug("Request to get all OrganizationUsers");
        return organizationUserRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizationUser> findOne(Long id) {
        log.debug("Request to get OrganizationUser : {}", id);
        return organizationUserRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete OrganizationUser : {}", id);
        organizationUserRepository.deleteById(id);
        organizationUserSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationUser> search(String query) {
        log.debug("Request to search OrganizationUsers for query {}", query);
        try {
            return StreamSupport.stream(organizationUserSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
