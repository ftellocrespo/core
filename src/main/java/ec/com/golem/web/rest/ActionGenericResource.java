package ec.com.golem.web.rest;

import ec.com.golem.domain.ActionGeneric;
import ec.com.golem.repository.ActionGenericRepository;
import ec.com.golem.repository.search.ActionGenericSearchRepository;
import ec.com.golem.web.rest.errors.BadRequestAlertException;
import ec.com.golem.web.rest.errors.ElasticsearchExceptionMapper;
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
 * REST controller for managing {@link ec.com.golem.domain.ActionGeneric}.
 */
@RestController
@RequestMapping("/api/action-generics")
@Transactional
public class ActionGenericResource {

    private final Logger log = LoggerFactory.getLogger(ActionGenericResource.class);

    private static final String ENTITY_NAME = "actionGeneric";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ActionGenericRepository actionGenericRepository;

    private final ActionGenericSearchRepository actionGenericSearchRepository;

    public ActionGenericResource(
        ActionGenericRepository actionGenericRepository,
        ActionGenericSearchRepository actionGenericSearchRepository
    ) {
        this.actionGenericRepository = actionGenericRepository;
        this.actionGenericSearchRepository = actionGenericSearchRepository;
    }

    /**
     * {@code POST  /action-generics} : Create a new actionGeneric.
     *
     * @param actionGeneric the actionGeneric to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new actionGeneric, or with status {@code 400 (Bad Request)} if the actionGeneric has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ActionGeneric> createActionGeneric(@RequestBody ActionGeneric actionGeneric) throws URISyntaxException {
        log.debug("REST request to save ActionGeneric : {}", actionGeneric);
        if (actionGeneric.getId() != null) {
            throw new BadRequestAlertException("A new actionGeneric cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ActionGeneric result = actionGenericRepository.save(actionGeneric);
        actionGenericSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/action-generics/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /action-generics/:id} : Updates an existing actionGeneric.
     *
     * @param id the id of the actionGeneric to save.
     * @param actionGeneric the actionGeneric to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actionGeneric,
     * or with status {@code 400 (Bad Request)} if the actionGeneric is not valid,
     * or with status {@code 500 (Internal Server Error)} if the actionGeneric couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ActionGeneric> updateActionGeneric(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ActionGeneric actionGeneric
    ) throws URISyntaxException {
        log.debug("REST request to update ActionGeneric : {}, {}", id, actionGeneric);
        if (actionGeneric.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actionGeneric.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionGenericRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ActionGeneric result = actionGenericRepository.save(actionGeneric);
        actionGenericSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, actionGeneric.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /action-generics/:id} : Partial updates given fields of an existing actionGeneric, field will ignore if it is null
     *
     * @param id the id of the actionGeneric to save.
     * @param actionGeneric the actionGeneric to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actionGeneric,
     * or with status {@code 400 (Bad Request)} if the actionGeneric is not valid,
     * or with status {@code 404 (Not Found)} if the actionGeneric is not found,
     * or with status {@code 500 (Internal Server Error)} if the actionGeneric couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ActionGeneric> partialUpdateActionGeneric(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ActionGeneric actionGeneric
    ) throws URISyntaxException {
        log.debug("REST request to partial update ActionGeneric partially : {}, {}", id, actionGeneric);
        if (actionGeneric.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actionGeneric.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actionGenericRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ActionGeneric> result = actionGenericRepository
            .findById(actionGeneric.getId())
            .map(existingActionGeneric -> {
                if (actionGeneric.getMessage() != null) {
                    existingActionGeneric.setMessage(actionGeneric.getMessage());
                }

                return existingActionGeneric;
            })
            .map(actionGenericRepository::save)
            .map(savedActionGeneric -> {
                actionGenericSearchRepository.index(savedActionGeneric);
                return savedActionGeneric;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, actionGeneric.getId().toString())
        );
    }

    /**
     * {@code GET  /action-generics} : get all the actionGenerics.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of actionGenerics in body.
     */
    @GetMapping("")
    public List<ActionGeneric> getAllActionGenerics() {
        log.debug("REST request to get all ActionGenerics");
        return actionGenericRepository.findAll();
    }

    /**
     * {@code GET  /action-generics/:id} : get the "id" actionGeneric.
     *
     * @param id the id of the actionGeneric to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the actionGeneric, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ActionGeneric> getActionGeneric(@PathVariable("id") Long id) {
        log.debug("REST request to get ActionGeneric : {}", id);
        Optional<ActionGeneric> actionGeneric = actionGenericRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(actionGeneric);
    }

    /**
     * {@code DELETE  /action-generics/:id} : delete the "id" actionGeneric.
     *
     * @param id the id of the actionGeneric to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActionGeneric(@PathVariable("id") Long id) {
        log.debug("REST request to delete ActionGeneric : {}", id);
        actionGenericRepository.deleteById(id);
        actionGenericSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /action-generics/_search?query=:query} : search for the actionGeneric corresponding
     * to the query.
     *
     * @param query the query of the actionGeneric search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<ActionGeneric> searchActionGenerics(@RequestParam("query") String query) {
        log.debug("REST request to search ActionGenerics for query {}", query);
        try {
            return StreamSupport.stream(actionGenericSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
