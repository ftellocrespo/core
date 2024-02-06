package ec.com.golem.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ec.com.golem.IntegrationTest;
import ec.com.golem.domain.UserX;
import ec.com.golem.domain.enumeration.AuthStateEnum;
import ec.com.golem.repository.UserXRepository;
import ec.com.golem.repository.search.UserXSearchRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserXResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserXResourceIT {

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_FACEBOOK_ID = "AAAAAAAAAA";
    private static final String UPDATED_FACEBOOK_ID = "BBBBBBBBBB";

    private static final String DEFAULT_GOOGLE_ID = "AAAAAAAAAA";
    private static final String UPDATED_GOOGLE_ID = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTH = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_GENDER = "AAAAAAAAAA";
    private static final String UPDATED_GENDER = "BBBBBBBBBB";

    private static final String DEFAULT_NATIONALITY = "AAAAAAAAAA";
    private static final String UPDATED_NATIONALITY = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final AuthStateEnum DEFAULT_STATE = AuthStateEnum.ACTIVE;
    private static final AuthStateEnum UPDATED_STATE = AuthStateEnum.INACTIVE;

    private static final String ENTITY_API_URL = "/api/user-xes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/user-xes/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserXRepository userXRepository;

    @Autowired
    private UserXSearchRepository userXSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserXMockMvc;

    private UserX userX;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserX createEntity(EntityManager em) {
        UserX userX = new UserX()
            .email(DEFAULT_EMAIL)
            .password(DEFAULT_PASSWORD)
            .facebookId(DEFAULT_FACEBOOK_ID)
            .googleId(DEFAULT_GOOGLE_ID)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .phone(DEFAULT_PHONE)
            .birth(DEFAULT_BIRTH)
            .gender(DEFAULT_GENDER)
            .nationality(DEFAULT_NATIONALITY)
            .address(DEFAULT_ADDRESS)
            .state(DEFAULT_STATE);
        return userX;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserX createUpdatedEntity(EntityManager em) {
        UserX userX = new UserX()
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .facebookId(UPDATED_FACEBOOK_ID)
            .googleId(UPDATED_GOOGLE_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phone(UPDATED_PHONE)
            .birth(UPDATED_BIRTH)
            .gender(UPDATED_GENDER)
            .nationality(UPDATED_NATIONALITY)
            .address(UPDATED_ADDRESS)
            .state(UPDATED_STATE);
        return userX;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        userXSearchRepository.deleteAll();
        assertThat(userXSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        userX = createEntity(em);
    }

    @Test
    @Transactional
    void createUserX() throws Exception {
        int databaseSizeBeforeCreate = userXRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userXSearchRepository.findAll());
        // Create the UserX
        restUserXMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userX)))
            .andExpect(status().isCreated());

        // Validate the UserX in the database
        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userXSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        UserX testUserX = userXList.get(userXList.size() - 1);
        assertThat(testUserX.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUserX.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testUserX.getFacebookId()).isEqualTo(DEFAULT_FACEBOOK_ID);
        assertThat(testUserX.getGoogleId()).isEqualTo(DEFAULT_GOOGLE_ID);
        assertThat(testUserX.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testUserX.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testUserX.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testUserX.getBirth()).isEqualTo(DEFAULT_BIRTH);
        assertThat(testUserX.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testUserX.getNationality()).isEqualTo(DEFAULT_NATIONALITY);
        assertThat(testUserX.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testUserX.getState()).isEqualTo(DEFAULT_STATE);
    }

    @Test
    @Transactional
    void createUserXWithExistingId() throws Exception {
        // Create the UserX with an existing ID
        userX.setId(1L);

        int databaseSizeBeforeCreate = userXRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userXSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserXMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userX)))
            .andExpect(status().isBadRequest());

        // Validate the UserX in the database
        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userXSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = userXRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userXSearchRepository.findAll());
        // set the field null
        userX.setEmail(null);

        // Create the UserX, which fails.

        restUserXMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userX)))
            .andExpect(status().isBadRequest());

        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userXSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPasswordIsRequired() throws Exception {
        int databaseSizeBeforeTest = userXRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userXSearchRepository.findAll());
        // set the field null
        userX.setPassword(null);

        // Create the UserX, which fails.

        restUserXMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userX)))
            .andExpect(status().isBadRequest());

        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userXSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStateIsRequired() throws Exception {
        int databaseSizeBeforeTest = userXRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userXSearchRepository.findAll());
        // set the field null
        userX.setState(null);

        // Create the UserX, which fails.

        restUserXMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userX)))
            .andExpect(status().isBadRequest());

        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userXSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllUserXES() throws Exception {
        // Initialize the database
        userXRepository.saveAndFlush(userX);

        // Get all the userXList
        restUserXMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userX.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].facebookId").value(hasItem(DEFAULT_FACEBOOK_ID)))
            .andExpect(jsonPath("$.[*].googleId").value(hasItem(DEFAULT_GOOGLE_ID)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].birth").value(hasItem(DEFAULT_BIRTH.toString())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER)))
            .andExpect(jsonPath("$.[*].nationality").value(hasItem(DEFAULT_NATIONALITY)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())));
    }

    @Test
    @Transactional
    void getUserX() throws Exception {
        // Initialize the database
        userXRepository.saveAndFlush(userX);

        // Get the userX
        restUserXMockMvc
            .perform(get(ENTITY_API_URL_ID, userX.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userX.getId().intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.facebookId").value(DEFAULT_FACEBOOK_ID))
            .andExpect(jsonPath("$.googleId").value(DEFAULT_GOOGLE_ID))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.birth").value(DEFAULT_BIRTH.toString()))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER))
            .andExpect(jsonPath("$.nationality").value(DEFAULT_NATIONALITY))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUserX() throws Exception {
        // Get the userX
        restUserXMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserX() throws Exception {
        // Initialize the database
        userXRepository.saveAndFlush(userX);

        int databaseSizeBeforeUpdate = userXRepository.findAll().size();
        userXSearchRepository.save(userX);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userXSearchRepository.findAll());

        // Update the userX
        UserX updatedUserX = userXRepository.findById(userX.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserX are not directly saved in db
        em.detach(updatedUserX);
        updatedUserX
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .facebookId(UPDATED_FACEBOOK_ID)
            .googleId(UPDATED_GOOGLE_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phone(UPDATED_PHONE)
            .birth(UPDATED_BIRTH)
            .gender(UPDATED_GENDER)
            .nationality(UPDATED_NATIONALITY)
            .address(UPDATED_ADDRESS)
            .state(UPDATED_STATE);

        restUserXMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserX.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserX))
            )
            .andExpect(status().isOk());

        // Validate the UserX in the database
        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeUpdate);
        UserX testUserX = userXList.get(userXList.size() - 1);
        assertThat(testUserX.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUserX.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUserX.getFacebookId()).isEqualTo(UPDATED_FACEBOOK_ID);
        assertThat(testUserX.getGoogleId()).isEqualTo(UPDATED_GOOGLE_ID);
        assertThat(testUserX.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUserX.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testUserX.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testUserX.getBirth()).isEqualTo(UPDATED_BIRTH);
        assertThat(testUserX.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testUserX.getNationality()).isEqualTo(UPDATED_NATIONALITY);
        assertThat(testUserX.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testUserX.getState()).isEqualTo(UPDATED_STATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userXSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UserX> userXSearchList = IterableUtils.toList(userXSearchRepository.findAll());
                UserX testUserXSearch = userXSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testUserXSearch.getEmail()).isEqualTo(UPDATED_EMAIL);
                assertThat(testUserXSearch.getPassword()).isEqualTo(UPDATED_PASSWORD);
                assertThat(testUserXSearch.getFacebookId()).isEqualTo(UPDATED_FACEBOOK_ID);
                assertThat(testUserXSearch.getGoogleId()).isEqualTo(UPDATED_GOOGLE_ID);
                assertThat(testUserXSearch.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
                assertThat(testUserXSearch.getLastName()).isEqualTo(UPDATED_LAST_NAME);
                assertThat(testUserXSearch.getPhone()).isEqualTo(UPDATED_PHONE);
                assertThat(testUserXSearch.getBirth()).isEqualTo(UPDATED_BIRTH);
                assertThat(testUserXSearch.getGender()).isEqualTo(UPDATED_GENDER);
                assertThat(testUserXSearch.getNationality()).isEqualTo(UPDATED_NATIONALITY);
                assertThat(testUserXSearch.getAddress()).isEqualTo(UPDATED_ADDRESS);
                assertThat(testUserXSearch.getState()).isEqualTo(UPDATED_STATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingUserX() throws Exception {
        int databaseSizeBeforeUpdate = userXRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userXSearchRepository.findAll());
        userX.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserXMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userX.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userX))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserX in the database
        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userXSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserX() throws Exception {
        int databaseSizeBeforeUpdate = userXRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userXSearchRepository.findAll());
        userX.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserXMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userX))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserX in the database
        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userXSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserX() throws Exception {
        int databaseSizeBeforeUpdate = userXRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userXSearchRepository.findAll());
        userX.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserXMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userX)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserX in the database
        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userXSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateUserXWithPatch() throws Exception {
        // Initialize the database
        userXRepository.saveAndFlush(userX);

        int databaseSizeBeforeUpdate = userXRepository.findAll().size();

        // Update the userX using partial update
        UserX partialUpdatedUserX = new UserX();
        partialUpdatedUserX.setId(userX.getId());

        partialUpdatedUserX.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).nationality(UPDATED_NATIONALITY);

        restUserXMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserX.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserX))
            )
            .andExpect(status().isOk());

        // Validate the UserX in the database
        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeUpdate);
        UserX testUserX = userXList.get(userXList.size() - 1);
        assertThat(testUserX.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUserX.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testUserX.getFacebookId()).isEqualTo(DEFAULT_FACEBOOK_ID);
        assertThat(testUserX.getGoogleId()).isEqualTo(DEFAULT_GOOGLE_ID);
        assertThat(testUserX.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUserX.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testUserX.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testUserX.getBirth()).isEqualTo(DEFAULT_BIRTH);
        assertThat(testUserX.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testUserX.getNationality()).isEqualTo(UPDATED_NATIONALITY);
        assertThat(testUserX.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testUserX.getState()).isEqualTo(DEFAULT_STATE);
    }

    @Test
    @Transactional
    void fullUpdateUserXWithPatch() throws Exception {
        // Initialize the database
        userXRepository.saveAndFlush(userX);

        int databaseSizeBeforeUpdate = userXRepository.findAll().size();

        // Update the userX using partial update
        UserX partialUpdatedUserX = new UserX();
        partialUpdatedUserX.setId(userX.getId());

        partialUpdatedUserX
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .facebookId(UPDATED_FACEBOOK_ID)
            .googleId(UPDATED_GOOGLE_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phone(UPDATED_PHONE)
            .birth(UPDATED_BIRTH)
            .gender(UPDATED_GENDER)
            .nationality(UPDATED_NATIONALITY)
            .address(UPDATED_ADDRESS)
            .state(UPDATED_STATE);

        restUserXMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserX.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserX))
            )
            .andExpect(status().isOk());

        // Validate the UserX in the database
        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeUpdate);
        UserX testUserX = userXList.get(userXList.size() - 1);
        assertThat(testUserX.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUserX.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUserX.getFacebookId()).isEqualTo(UPDATED_FACEBOOK_ID);
        assertThat(testUserX.getGoogleId()).isEqualTo(UPDATED_GOOGLE_ID);
        assertThat(testUserX.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUserX.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testUserX.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testUserX.getBirth()).isEqualTo(UPDATED_BIRTH);
        assertThat(testUserX.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testUserX.getNationality()).isEqualTo(UPDATED_NATIONALITY);
        assertThat(testUserX.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testUserX.getState()).isEqualTo(UPDATED_STATE);
    }

    @Test
    @Transactional
    void patchNonExistingUserX() throws Exception {
        int databaseSizeBeforeUpdate = userXRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userXSearchRepository.findAll());
        userX.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserXMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userX.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userX))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserX in the database
        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userXSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserX() throws Exception {
        int databaseSizeBeforeUpdate = userXRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userXSearchRepository.findAll());
        userX.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserXMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userX))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserX in the database
        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userXSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserX() throws Exception {
        int databaseSizeBeforeUpdate = userXRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userXSearchRepository.findAll());
        userX.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserXMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userX)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserX in the database
        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userXSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteUserX() throws Exception {
        // Initialize the database
        userXRepository.saveAndFlush(userX);
        userXRepository.save(userX);
        userXSearchRepository.save(userX);

        int databaseSizeBeforeDelete = userXRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userXSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the userX
        restUserXMockMvc
            .perform(delete(ENTITY_API_URL_ID, userX.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserX> userXList = userXRepository.findAll();
        assertThat(userXList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userXSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchUserX() throws Exception {
        // Initialize the database
        userX = userXRepository.saveAndFlush(userX);
        userXSearchRepository.save(userX);

        // Search the userX
        restUserXMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + userX.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userX.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].facebookId").value(hasItem(DEFAULT_FACEBOOK_ID)))
            .andExpect(jsonPath("$.[*].googleId").value(hasItem(DEFAULT_GOOGLE_ID)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].birth").value(hasItem(DEFAULT_BIRTH.toString())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER)))
            .andExpect(jsonPath("$.[*].nationality").value(hasItem(DEFAULT_NATIONALITY)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())));
    }
}
