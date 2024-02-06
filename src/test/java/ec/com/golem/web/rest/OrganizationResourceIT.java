package ec.com.golem.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ec.com.golem.IntegrationTest;
import ec.com.golem.domain.Organization;
import ec.com.golem.domain.enumeration.AuthStateEnum;
import ec.com.golem.domain.enumeration.CountryEnum;
import ec.com.golem.repository.OrganizationRepository;
import ec.com.golem.repository.search.OrganizationSearchRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link OrganizationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrganizationResourceIT {

    private static final String DEFAULT_IDENTIFICATION = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFICATION = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final AuthStateEnum DEFAULT_STATE = AuthStateEnum.ACTIVE;
    private static final AuthStateEnum UPDATED_STATE = AuthStateEnum.INACTIVE;

    private static final CountryEnum DEFAULT_COUNTRY = CountryEnum.ECUADOR;
    private static final CountryEnum UPDATED_COUNTRY = CountryEnum.ECUADOR;

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/organizations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/organizations/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationSearchRepository organizationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrganizationMockMvc;

    private Organization organization;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organization createEntity(EntityManager em) {
        Organization organization = new Organization()
            .identification(DEFAULT_IDENTIFICATION)
            .name(DEFAULT_NAME)
            .state(DEFAULT_STATE)
            .country(DEFAULT_COUNTRY)
            .address(DEFAULT_ADDRESS)
            .phone(DEFAULT_PHONE)
            .email(DEFAULT_EMAIL);
        return organization;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organization createUpdatedEntity(EntityManager em) {
        Organization organization = new Organization()
            .identification(UPDATED_IDENTIFICATION)
            .name(UPDATED_NAME)
            .state(UPDATED_STATE)
            .country(UPDATED_COUNTRY)
            .address(UPDATED_ADDRESS)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL);
        return organization;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        organizationSearchRepository.deleteAll();
        assertThat(organizationSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        organization = createEntity(em);
    }

    @Test
    @Transactional
    void createOrganization() throws Exception {
        int databaseSizeBeforeCreate = organizationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        // Create the Organization
        restOrganizationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organization)))
            .andExpect(status().isCreated());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getIdentification()).isEqualTo(DEFAULT_IDENTIFICATION);
        assertThat(testOrganization.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrganization.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testOrganization.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testOrganization.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testOrganization.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testOrganization.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void createOrganizationWithExistingId() throws Exception {
        // Create the Organization with an existing ID
        organization.setId(1L);

        int databaseSizeBeforeCreate = organizationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrganizationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organization)))
            .andExpect(status().isBadRequest());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIdentificationIsRequired() throws Exception {
        int databaseSizeBeforeTest = organizationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        // set the field null
        organization.setIdentification(null);

        // Create the Organization, which fails.

        restOrganizationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organization)))
            .andExpect(status().isBadRequest());

        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = organizationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        // set the field null
        organization.setName(null);

        // Create the Organization, which fails.

        restOrganizationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organization)))
            .andExpect(status().isBadRequest());

        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStateIsRequired() throws Exception {
        int databaseSizeBeforeTest = organizationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        // set the field null
        organization.setState(null);

        // Create the Organization, which fails.

        restOrganizationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organization)))
            .andExpect(status().isBadRequest());

        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOrganizations() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList
        restOrganizationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organization.getId().intValue())))
            .andExpect(jsonPath("$.[*].identification").value(hasItem(DEFAULT_IDENTIFICATION)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }

    @Test
    @Transactional
    void getOrganization() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get the organization
        restOrganizationMockMvc
            .perform(get(ENTITY_API_URL_ID, organization.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(organization.getId().intValue()))
            .andExpect(jsonPath("$.identification").value(DEFAULT_IDENTIFICATION))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
    }

    @Test
    @Transactional
    void getNonExistingOrganization() throws Exception {
        // Get the organization
        restOrganizationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrganization() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();
        organizationSearchRepository.save(organization);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationSearchRepository.findAll());

        // Update the organization
        Organization updatedOrganization = organizationRepository.findById(organization.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrganization are not directly saved in db
        em.detach(updatedOrganization);
        updatedOrganization
            .identification(UPDATED_IDENTIFICATION)
            .name(UPDATED_NAME)
            .state(UPDATED_STATE)
            .country(UPDATED_COUNTRY)
            .address(UPDATED_ADDRESS)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL);

        restOrganizationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrganization.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrganization))
            )
            .andExpect(status().isOk());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getIdentification()).isEqualTo(UPDATED_IDENTIFICATION);
        assertThat(testOrganization.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrganization.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testOrganization.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testOrganization.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testOrganization.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testOrganization.getEmail()).isEqualTo(UPDATED_EMAIL);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Organization> organizationSearchList = IterableUtils.toList(organizationSearchRepository.findAll());
                Organization testOrganizationSearch = organizationSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testOrganizationSearch.getIdentification()).isEqualTo(UPDATED_IDENTIFICATION);
                assertThat(testOrganizationSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testOrganizationSearch.getState()).isEqualTo(UPDATED_STATE);
                assertThat(testOrganizationSearch.getCountry()).isEqualTo(UPDATED_COUNTRY);
                assertThat(testOrganizationSearch.getAddress()).isEqualTo(UPDATED_ADDRESS);
                assertThat(testOrganizationSearch.getPhone()).isEqualTo(UPDATED_PHONE);
                assertThat(testOrganizationSearch.getEmail()).isEqualTo(UPDATED_EMAIL);
            });
    }

    @Test
    @Transactional
    void putNonExistingOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        organization.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganizationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, organization.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(organization))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        organization.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(organization))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        organization.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organization)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOrganizationWithPatch() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();

        // Update the organization using partial update
        Organization partialUpdatedOrganization = new Organization();
        partialUpdatedOrganization.setId(organization.getId());

        partialUpdatedOrganization
            .identification(UPDATED_IDENTIFICATION)
            .country(UPDATED_COUNTRY)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL);

        restOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganization.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganization))
            )
            .andExpect(status().isOk());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getIdentification()).isEqualTo(UPDATED_IDENTIFICATION);
        assertThat(testOrganization.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrganization.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testOrganization.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testOrganization.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testOrganization.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testOrganization.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void fullUpdateOrganizationWithPatch() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();

        // Update the organization using partial update
        Organization partialUpdatedOrganization = new Organization();
        partialUpdatedOrganization.setId(organization.getId());

        partialUpdatedOrganization
            .identification(UPDATED_IDENTIFICATION)
            .name(UPDATED_NAME)
            .state(UPDATED_STATE)
            .country(UPDATED_COUNTRY)
            .address(UPDATED_ADDRESS)
            .phone(UPDATED_PHONE)
            .email(UPDATED_EMAIL);

        restOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganization.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganization))
            )
            .andExpect(status().isOk());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getIdentification()).isEqualTo(UPDATED_IDENTIFICATION);
        assertThat(testOrganization.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrganization.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testOrganization.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testOrganization.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testOrganization.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testOrganization.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void patchNonExistingOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        organization.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, organization.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(organization))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        organization.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(organization))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        organization.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(organization))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOrganization() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);
        organizationRepository.save(organization);
        organizationSearchRepository.save(organization);

        int databaseSizeBeforeDelete = organizationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the organization
        restOrganizationMockMvc
            .perform(delete(ENTITY_API_URL_ID, organization.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOrganization() throws Exception {
        // Initialize the database
        organization = organizationRepository.saveAndFlush(organization);
        organizationSearchRepository.save(organization);

        // Search the organization
        restOrganizationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + organization.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organization.getId().intValue())))
            .andExpect(jsonPath("$.[*].identification").value(hasItem(DEFAULT_IDENTIFICATION)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }
}
