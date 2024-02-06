package ec.com.golem.web.rest;

import ec.com.golem.domain.Process;
import ec.com.golem.repository.ProcessRepository;
import ec.com.golem.repository.search.ProcessSearchRepository;
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
 * REST controller for managing {@link ec.com.golem.domain.Process}.
 */
@RestController
@RequestMapping("/api/processes")
@Transactional
public class ProcessResource {

    private final Logger log = LoggerFactory.getLogger(ProcessResource.class);

    private static final String ENTITY_NAME = "process";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProcessRepository processRepository;

    private final ProcessSearchRepository processSearchRepository;

    public ProcessResource(ProcessRepository processRepository, ProcessSearchRepository processSearchRepository) {
        this.processRepository = processRepository;
        this.processSearchRepository = processSearchRepository;
    }

    /**
     * {@code POST  /processes} : Create a new process.
     *
     * @param process the process to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new process, or with status {@code 400 (Bad Request)} if the process has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Process> createProcess(@RequestBody Process process) throws URISyntaxException {
        log.debug("REST request to save Process : {}", process);
        if (process.getId() != null) {
            throw new BadRequestAlertException("A new process cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Process result = processRepository.save(process);
        processSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/processes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /processes/:id} : Updates an existing process.
     *
     * @param id the id of the process to save.
     * @param process the process to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated process,
     * or with status {@code 400 (Bad Request)} if the process is not valid,
     * or with status {@code 500 (Internal Server Error)} if the process couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Process> updateProcess(@PathVariable(value = "id", required = false) final Long id, @RequestBody Process process)
        throws URISyntaxException {
        log.debug("REST request to update Process : {}, {}", id, process);
        if (process.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, process.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!processRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Process result = processRepository.save(process);
        processSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, process.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /processes/:id} : Partial updates given fields of an existing process, field will ignore if it is null
     *
     * @param id the id of the process to save.
     * @param process the process to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated process,
     * or with status {@code 400 (Bad Request)} if the process is not valid,
     * or with status {@code 404 (Not Found)} if the process is not found,
     * or with status {@code 500 (Internal Server Error)} if the process couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Process> partialUpdateProcess(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Process process
    ) throws URISyntaxException {
        log.debug("REST request to partial update Process partially : {}, {}", id, process);
        if (process.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, process.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!processRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Process> result = processRepository
            .findById(process.getId())
            .map(existingProcess -> {
                if (process.getName() != null) {
                    existingProcess.setName(process.getName());
                }
                if (process.getMeta() != null) {
                    existingProcess.setMeta(process.getMeta());
                }

                return existingProcess;
            })
            .map(processRepository::save)
            .map(savedProcess -> {
                processSearchRepository.index(savedProcess);
                return savedProcess;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, process.getId().toString())
        );
    }

    /**
     * {@code GET  /processes} : get all the processes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of processes in body.
     */
    @GetMapping("")
    public List<Process> getAllProcesses() {
        log.debug("REST request to get all Processes");
        return processRepository.findAll();
    }

    /**
     * {@code GET  /processes/:id} : get the "id" process.
     *
     * @param id the id of the process to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the process, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Process> getProcess(@PathVariable("id") Long id) {
        log.debug("REST request to get Process : {}", id);
        Optional<Process> process = processRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(process);
    }

    /**
     * {@code DELETE  /processes/:id} : delete the "id" process.
     *
     * @param id the id of the process to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProcess(@PathVariable("id") Long id) {
        log.debug("REST request to delete Process : {}", id);
        processRepository.deleteById(id);
        processSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /processes/_search?query=:query} : search for the process corresponding
     * to the query.
     *
     * @param query the query of the process search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Process> searchProcesses(@RequestParam("query") String query) {
        log.debug("REST request to search Processes for query {}", query);
        try {
            return StreamSupport.stream(processSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
