package ec.com.golem.web.rest;

import ec.com.golem.domain.OrganizationUser;
import ec.com.golem.repository.OrganizationUserRepository;
import ec.com.golem.service.OrganizationUserService;
import ec.com.golem.web.rest.errors.BadRequestAlertException;
import ec.com.golem.web.rest.errors.ElasticsearchExceptionMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ec.com.golem.domain.OrganizationUser}.
 */
@RestController
@RequestMapping("/api/organization-users")
public class OrganizationUserResource {

    private final Logger log = LoggerFactory.getLogger(OrganizationUserResource.class);

    private static final String ENTITY_NAME = "organizationUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrganizationUserService organizationUserService;

    private final OrganizationUserRepository organizationUserRepository;

    public OrganizationUserResource(
        OrganizationUserService organizationUserService,
        OrganizationUserRepository organizationUserRepository
    ) {
        this.organizationUserService = organizationUserService;
        this.organizationUserRepository = organizationUserRepository;
    }

    /**
     * {@code POST  /organization-users} : Create a new organizationUser.
     *
     * @param organizationUser the organizationUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new organizationUser, or with status {@code 400 (Bad Request)} if the organizationUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OrganizationUser> createOrganizationUser(@RequestBody OrganizationUser organizationUser)
        throws URISyntaxException {
        log.debug("REST request to save OrganizationUser : {}", organizationUser);
        if (organizationUser.getId() != null) {
            throw new BadRequestAlertException("A new organizationUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrganizationUser result = organizationUserService.save(organizationUser);
        return ResponseEntity
            .created(new URI("/api/organization-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /organization-users/:id} : Updates an existing organizationUser.
     *
     * @param id the id of the organizationUser to save.
     * @param organizationUser the organizationUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organizationUser,
     * or with status {@code 400 (Bad Request)} if the organizationUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the organizationUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrganizationUser> updateOrganizationUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrganizationUser organizationUser
    ) throws URISyntaxException {
        log.debug("REST request to update OrganizationUser : {}, {}", id, organizationUser);
        if (organizationUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organizationUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!organizationUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OrganizationUser result = organizationUserService.update(organizationUser);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organizationUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /organization-users/:id} : Partial updates given fields of an existing organizationUser, field will ignore if it is null
     *
     * @param id the id of the organizationUser to save.
     * @param organizationUser the organizationUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organizationUser,
     * or with status {@code 400 (Bad Request)} if the organizationUser is not valid,
     * or with status {@code 404 (Not Found)} if the organizationUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the organizationUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrganizationUser> partialUpdateOrganizationUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrganizationUser organizationUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrganizationUser partially : {}, {}", id, organizationUser);
        if (organizationUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organizationUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!organizationUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrganizationUser> result = organizationUserService.partialUpdate(organizationUser);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organizationUser.getId().toString())
        );
    }

    /**
     * {@code GET  /organization-users} : get all the organizationUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of organizationUsers in body.
     */
    @GetMapping("")
    public List<OrganizationUser> getAllOrganizationUsers() {
        log.debug("REST request to get all OrganizationUsers");
        return organizationUserService.findAll();
    }

    /**
     * {@code GET  /organization-users/:id} : get the "id" organizationUser.
     *
     * @param id the id of the organizationUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the organizationUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationUser> getOrganizationUser(@PathVariable("id") Long id) {
        log.debug("REST request to get OrganizationUser : {}", id);
        Optional<OrganizationUser> organizationUser = organizationUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(organizationUser);
    }

    /**
     * {@code DELETE  /organization-users/:id} : delete the "id" organizationUser.
     *
     * @param id the id of the organizationUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganizationUser(@PathVariable("id") Long id) {
        log.debug("REST request to delete OrganizationUser : {}", id);
        organizationUserService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /organization-users/_search?query=:query} : search for the organizationUser corresponding
     * to the query.
     *
     * @param query the query of the organizationUser search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<OrganizationUser> searchOrganizationUsers(@RequestParam("query") String query) {
        log.debug("REST request to search OrganizationUsers for query {}", query);
        try {
            return organizationUserService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
