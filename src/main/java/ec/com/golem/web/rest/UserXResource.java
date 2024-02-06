package ec.com.golem.web.rest;

import ec.com.golem.domain.UserX;
import ec.com.golem.repository.UserXRepository;
import ec.com.golem.repository.search.UserXSearchRepository;
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
 * REST controller for managing {@link ec.com.golem.domain.UserX}.
 */
@RestController
@RequestMapping("/api/user-xes")
@Transactional
public class UserXResource {

    private final Logger log = LoggerFactory.getLogger(UserXResource.class);

    private static final String ENTITY_NAME = "userX";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserXRepository userXRepository;

    private final UserXSearchRepository userXSearchRepository;

    public UserXResource(UserXRepository userXRepository, UserXSearchRepository userXSearchRepository) {
        this.userXRepository = userXRepository;
        this.userXSearchRepository = userXSearchRepository;
    }

    /**
     * {@code POST  /user-xes} : Create a new userX.
     *
     * @param userX the userX to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userX, or with status {@code 400 (Bad Request)} if the userX has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserX> createUserX(@Valid @RequestBody UserX userX) throws URISyntaxException {
        log.debug("REST request to save UserX : {}", userX);
        if (userX.getId() != null) {
            throw new BadRequestAlertException("A new userX cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserX result = userXRepository.save(userX);
        userXSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/user-xes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-xes/:id} : Updates an existing userX.
     *
     * @param id the id of the userX to save.
     * @param userX the userX to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userX,
     * or with status {@code 400 (Bad Request)} if the userX is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userX couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserX> updateUserX(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody UserX userX)
        throws URISyntaxException {
        log.debug("REST request to update UserX : {}, {}", id, userX);
        if (userX.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userX.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userXRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UserX result = userXRepository.save(userX);
        userXSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userX.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-xes/:id} : Partial updates given fields of an existing userX, field will ignore if it is null
     *
     * @param id the id of the userX to save.
     * @param userX the userX to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userX,
     * or with status {@code 400 (Bad Request)} if the userX is not valid,
     * or with status {@code 404 (Not Found)} if the userX is not found,
     * or with status {@code 500 (Internal Server Error)} if the userX couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserX> partialUpdateUserX(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserX userX
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserX partially : {}, {}", id, userX);
        if (userX.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userX.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userXRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserX> result = userXRepository
            .findById(userX.getId())
            .map(existingUserX -> {
                if (userX.getEmail() != null) {
                    existingUserX.setEmail(userX.getEmail());
                }
                if (userX.getPassword() != null) {
                    existingUserX.setPassword(userX.getPassword());
                }
                if (userX.getFacebookId() != null) {
                    existingUserX.setFacebookId(userX.getFacebookId());
                }
                if (userX.getGoogleId() != null) {
                    existingUserX.setGoogleId(userX.getGoogleId());
                }
                if (userX.getFirstName() != null) {
                    existingUserX.setFirstName(userX.getFirstName());
                }
                if (userX.getLastName() != null) {
                    existingUserX.setLastName(userX.getLastName());
                }
                if (userX.getPhone() != null) {
                    existingUserX.setPhone(userX.getPhone());
                }
                if (userX.getBirth() != null) {
                    existingUserX.setBirth(userX.getBirth());
                }
                if (userX.getGender() != null) {
                    existingUserX.setGender(userX.getGender());
                }
                if (userX.getNationality() != null) {
                    existingUserX.setNationality(userX.getNationality());
                }
                if (userX.getAddress() != null) {
                    existingUserX.setAddress(userX.getAddress());
                }
                if (userX.getState() != null) {
                    existingUserX.setState(userX.getState());
                }

                return existingUserX;
            })
            .map(userXRepository::save)
            .map(savedUserX -> {
                userXSearchRepository.index(savedUserX);
                return savedUserX;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userX.getId().toString())
        );
    }

    /**
     * {@code GET  /user-xes} : get all the userXES.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userXES in body.
     */
    @GetMapping("")
    public List<UserX> getAllUserXES() {
        log.debug("REST request to get all UserXES");
        return userXRepository.findAll();
    }

    /**
     * {@code GET  /user-xes/:id} : get the "id" userX.
     *
     * @param id the id of the userX to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userX, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserX> getUserX(@PathVariable("id") Long id) {
        log.debug("REST request to get UserX : {}", id);
        Optional<UserX> userX = userXRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(userX);
    }

    /**
     * {@code DELETE  /user-xes/:id} : delete the "id" userX.
     *
     * @param id the id of the userX to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserX(@PathVariable("id") Long id) {
        log.debug("REST request to delete UserX : {}", id);
        userXRepository.deleteById(id);
        userXSearchRepository.deleteFromIndexById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /user-xes/_search?query=:query} : search for the userX corresponding
     * to the query.
     *
     * @param query the query of the userX search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<UserX> searchUserXES(@RequestParam("query") String query) {
        log.debug("REST request to search UserXES for query {}", query);
        try {
            return StreamSupport.stream(userXSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
