package ec.com.golem.web.rest;

import ec.com.golem.domain.ErrorLog;
import ec.com.golem.repository.ErrorLogRepository;
import ec.com.golem.repository.search.ErrorLogSearchRepository;
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
 * REST controller for managing {@link ec.com.golem.domain.ErrorLog}.
 */
@RestController
@RequestMapping("/api/error-logs")
@Transactional
public class ErrorLogResource {

    private final Logger log = LoggerFactory.getLogger(ErrorLogResource.class);

    private static final String ENTITY_NAME = "errorLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ErrorLogRepository errorLogRepository;

    private final ErrorLogSearchRepository errorLogSearchRepository;

    public ErrorLogResource(ErrorLogRepository errorLogRepository, ErrorLogSearchRepository errorLogSearchRepository) {
        this.errorLogRepository = errorLogRepository;
        this.errorLogSearchRepository = errorLogSearchRepository;
    }

    /**
     * {@code POST  /error-logs} : Create a new errorLog.
     *
     * @param errorLog the errorLog to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new errorLog, or with status {@code 400 (Bad Request)} if the errorLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ErrorLog> createErrorLog(@RequestBody ErrorLog errorLog) throws URISyntaxException {
        log.debug("REST request to save ErrorLog : {}", errorLog);
        if (errorLog.getId() != null) {
            throw new BadRequestAlertException("A new errorLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ErrorLog result = errorLogRepository.save(errorLog);
        errorLogSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/error-logs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /error-logs/:id} : Updates an existing errorLog.
     *
     * @param id the id of the errorLog to save.
     * @param errorLog the errorLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated errorLog,
     * or with status {@code 400 (Bad Request)} if the errorLog is not valid,
     * or with status {@code 500 (Internal Server Error)} if the errorLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ErrorLog> updateErrorLog(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ErrorLog errorLog
    ) throws URISyntaxException {
        log.debug("REST request to update ErrorLog : {}, {}", id, errorLog);
        if (errorLog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, errorLog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!errorLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ErrorLog result = errorLogRepository.save(errorLog);
        errorLogSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, errorLog.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /error-logs/:id} : Partial updates given fields of an existing errorLog, field will ignore if it is null
     *
     * @param id the id of the errorLog to save.
     * @param errorLog the errorLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated errorLog,
     * or with status {@code 400 (Bad Request)} if the errorLog is not valid,
     * or with status {@code 404 (Not Found)} if the errorLog is not found,
     * or with status {@code 500 (Internal Server Error)} if the errorLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ErrorLog> partialUpdateErrorLog(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ErrorLog errorLog
    ) throws URISyntaxException {
        log.debug("REST request to partial update ErrorLog partially : {}, {}", id, errorLog);
        if (errorLog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, errorLog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!errorLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ErrorLog> result = errorLogRepository
            .findById(errorLog.getId())
            .map(existingErrorLog -> {
                if (errorLog.getClassName() != null) {
                    existingErrorLog.setClassName(errorLog.getClassName());
                }
                if (errorLog.getLine() != null) {
                    existingErrorLog.setLine(errorLog.getLine());
                }
                if (errorLog.getError() != null) {
                    existingErrorLog.setError(errorLog.getError());
                }
                if (errorLog.getErroyType() != null) {
                    existingErrorLog.setErroyType(errorLog.getErroyType());
                }
                if (errorLog.getTimestamp() != null) {
                    existingErrorLog.setTimestamp(errorLog.getTimestamp());
                }

                return existingErrorLog;
            })
            .map(errorLogRepository::save)
            .map(savedErrorLog -> {
                errorLogSearchRepository.index(savedErrorLog);
                return savedErrorLog;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, errorLog.getId().toString())
        );
    }

    /**
     * {@code GET  /error-logs} : get all the errorLogs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of errorLogs in body.
     */
    @GetMapping("")
    public List<ErrorLog> getAllErrorLogs() {
        log.debug("REST request to get all ErrorLogs");
        return errorLogRepository.findAll();
    }

    /**
     * {@code GET  /error-logs/:id} : get the "id" errorLog.
     *
     * @param id the id of the errorLog to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the errorLog, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ErrorLog> getErrorLog(@PathVariable("id") Long id) {
        log.debug("REST request to get ErrorLog : {}", id);
        Optional<ErrorLog> errorLog = errorLogRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(errorLog);
    }

    /**
     * {@code DELETE  /error-logs/:id} : delete the "id" errorLog.
     *
     * @param id the id of the errorLog to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteErrorLog(@PathVariable("id") Long id) {
        log.debug("REST request to delete ErrorLog : {}", id);
        errorLogRepository.deleteById(id);
        errorLogSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /error-logs/_search?query=:query} : search for the errorLog corresponding
     * to the query.
     *
     * @param query the query of the errorLog search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<ErrorLog> searchErrorLogs(@RequestParam("query") String query) {
        log.debug("REST request to search ErrorLogs for query {}", query);
        try {
            return StreamSupport.stream(errorLogSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
