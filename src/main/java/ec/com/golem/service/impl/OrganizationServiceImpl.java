package ec.com.golem.service.impl;

import ec.com.golem.domain.Organization;
import ec.com.golem.repository.OrganizationRepository;
import ec.com.golem.repository.search.OrganizationSearchRepository;
import ec.com.golem.service.OrganizationService;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ec.com.golem.domain.Organization}.
 */
@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private final Logger log = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private final OrganizationRepository organizationRepository;

    private final OrganizationSearchRepository organizationSearchRepository;

    public OrganizationServiceImpl(
        OrganizationRepository organizationRepository,
        OrganizationSearchRepository organizationSearchRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationSearchRepository = organizationSearchRepository;
    }

    @Override
    public Organization save(Organization organization) {
        log.debug("Request to save Organization : {}", organization);
        Organization result = organizationRepository.save(organization);
        organizationSearchRepository.index(result);
        return result;
    }

    @Override
    public Organization update(Organization organization) {
        log.debug("Request to update Organization : {}", organization);
        Organization result = organizationRepository.save(organization);
        organizationSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Organization> partialUpdate(Organization organization) {
        log.debug("Request to partially update Organization : {}", organization);

        return organizationRepository
            .findById(organization.getId())
            .map(existingOrganization -> {
                if (organization.getIdentification() != null) {
                    existingOrganization.setIdentification(organization.getIdentification());
                }
                if (organization.getName() != null) {
                    existingOrganization.setName(organization.getName());
                }
                if (organization.getState() != null) {
                    existingOrganization.setState(organization.getState());
                }
                if (organization.getCountry() != null) {
                    existingOrganization.setCountry(organization.getCountry());
                }
                if (organization.getAddress() != null) {
                    existingOrganization.setAddress(organization.getAddress());
                }
                if (organization.getPhone() != null) {
                    existingOrganization.setPhone(organization.getPhone());
                }
                if (organization.getEmail() != null) {
                    existingOrganization.setEmail(organization.getEmail());
                }

                return existingOrganization;
            })
            .map(organizationRepository::save)
            .map(savedOrganization -> {
                organizationSearchRepository.index(savedOrganization);
                return savedOrganization;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organization> findAll() {
        log.debug("Request to get all Organizations");
        return organizationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Organization> findOne(Long id) {
        log.debug("Request to get Organization : {}", id);
        return organizationRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Organization : {}", id);
        organizationRepository.deleteById(id);
        organizationSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organization> search(String query) {
        log.debug("Request to search Organizations for query {}", query);
        try {
            return StreamSupport.stream(organizationSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
