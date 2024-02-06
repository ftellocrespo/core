package ec.com.golem.web.rest;

import ec.com.golem.domain.Organization;
import ec.com.golem.repository.OrganizationRepository;
import ec.com.golem.repository.search.OrganizationSearchRepository;
import ec.com.golem.web.rest.errors.BadRequestAlertException;
import ec.com.golem.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ec.com.golem.domain.Organization}.
 */
@RestController
@RequestMapping("/api/organizations")
@Transactional
public class OrganizationResource {

    private final Logger log = LoggerFactory.getLogger(OrganizationResource.class);

    private static final String ENTITY_NAME = "organization";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrganizationRepository organizationRepository;

    private final OrganizationSearchRepository organizationSearchRepository;

    public OrganizationResource(OrganizationRepository organizationRepository, OrganizationSearchRepository organizationSearchRepository) {
        this.organizationRepository = organizationRepository;
        this.organizationSearchRepository = organizationSearchRepository;
    }

    /**
     * {@code POST  /organizations} : Create a new organization.
     *
     * @param organization the organization to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new organization, or with status {@code 400 (Bad Request)} if the organization has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Organization> createOrganization(@Valid @RequestBody Organization organization) throws URISyntaxException {
        log.debug("REST request to save Organization : {}", organization);
        if (organization.getId() != null) {
            throw new BadRequestAlertException("A new organization cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Organization result = organizationRepository.save(organization);
        organizationSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/organizations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /organizations/:id} : Updates an existing organization.
     *
     * @param id the id of the organization to save.
     * @param organization the organization to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organization,
     * or with status {@code 400 (Bad Request)} if the organization is not valid,
     * or with status {@code 500 (Internal Server Error)} if the organization couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Organization> updateOrganization(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Organization organization
    ) throws URISyntaxException {
        log.debug("REST request to update Organization : {}, {}", id, organization);
        if (organization.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organization.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!organizationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Organization result = organizationRepository.save(organization);
        organizationSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organization.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /organizations/:id} : Partial updates given fields of an existing organization, field will ignore if it is null
     *
     * @param id the id of the organization to save.
     * @param organization the organization to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organization,
     * or with status {@code 400 (Bad Request)} if the organization is not valid,
     * or with status {@code 404 (Not Found)} if the organization is not found,
     * or with status {@code 500 (Internal Server Error)} if the organization couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Organization> partialUpdateOrganization(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Organization organization
    ) throws URISyntaxException {
        log.debug("REST request to partial update Organization partially : {}, {}", id, organization);
        if (organization.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organization.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!organizationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Organization> result = organizationRepository
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

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organization.getId().toString())
        );
    }

    /**
     * {@code GET  /organizations} : get all the organizations.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of organizations in body.
     */
    @GetMapping("")
    public List<Organization> getAllOrganizations() {
        log.debug("REST request to get all Organizations");
        return organizationRepository.findAll();
    }

    /**
     * {@code GET  /organizations/:id} : get the "id" organization.
     *
     * @param id the id of the organization to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the organization, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganization(@PathVariable("id") Long id) {
        log.debug("REST request to get Organization : {}", id);
        Optional<Organization> organization = organizationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(organization);
    }

    /**
     * {@code DELETE  /organizations/:id} : delete the "id" organization.
     *
     * @param id the id of the organization to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable("id") Long id) {
        log.debug("REST request to delete Organization : {}", id);
        organizationRepository.deleteById(id);
        organizationSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /organizations/_search?query=:query} : search for the organization corresponding
     * to the query.
     *
     * @param query the query of the organization search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Organization> searchOrganizations(@RequestParam("query") String query) {
        log.debug("REST request to search Organizations for query {}", query);
        try {
            return StreamSupport.stream(organizationSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
