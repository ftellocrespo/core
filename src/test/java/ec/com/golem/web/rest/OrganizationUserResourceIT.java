package ec.com.golem.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ec.com.golem.IntegrationTest;
import ec.com.golem.domain.OrganizationUser;
import ec.com.golem.domain.enumeration.AuthStateEnum;
import ec.com.golem.domain.enumeration.RoleEnum;
import ec.com.golem.repository.OrganizationUserRepository;
import ec.com.golem.repository.search.OrganizationUserSearchRepository;
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
 * Integration tests for the {@link OrganizationUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrganizationUserResourceIT {

    private static final AuthStateEnum DEFAULT_STATE = AuthStateEnum.ACTIVE;
    private static final AuthStateEnum UPDATED_STATE = AuthStateEnum.INACTIVE;

    private static final RoleEnum DEFAULT_ROLE = RoleEnum.ADMIN;
    private static final RoleEnum UPDATED_ROLE = RoleEnum.OR_ADMIN;

    private static final String ENTITY_API_URL = "/api/organization-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/organization-users/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrganizationUserRepository organizationUserRepository;

    @Autowired
    private OrganizationUserSearchRepository organizationUserSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrganizationUserMockMvc;

    private OrganizationUser organizationUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrganizationUser createEntity(EntityManager em) {
        OrganizationUser organizationUser = new OrganizationUser().state(DEFAULT_STATE).role(DEFAULT_ROLE);
        return organizationUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrganizationUser createUpdatedEntity(EntityManager em) {
        OrganizationUser organizationUser = new OrganizationUser().state(UPDATED_STATE).role(UPDATED_ROLE);
        return organizationUser;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        organizationUserSearchRepository.deleteAll();
        assertThat(organizationUserSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        organizationUser = createEntity(em);
    }

    @Test
    @Transactional
    void createOrganizationUser() throws Exception {
        int databaseSizeBeforeCreate = organizationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        // Create the OrganizationUser
        restOrganizationUserMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organizationUser))
            )
            .andExpect(status().isCreated());

        // Validate the OrganizationUser in the database
        List<OrganizationUser> organizationUserList = organizationUserRepository.findAll();
        assertThat(organizationUserList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        OrganizationUser testOrganizationUser = organizationUserList.get(organizationUserList.size() - 1);
        assertThat(testOrganizationUser.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testOrganizationUser.getRole()).isEqualTo(DEFAULT_ROLE);
    }

    @Test
    @Transactional
    void createOrganizationUserWithExistingId() throws Exception {
        // Create the OrganizationUser with an existing ID
        organizationUser.setId(1L);

        int databaseSizeBeforeCreate = organizationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrganizationUserMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organizationUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrganizationUser in the database
        List<OrganizationUser> organizationUserList = organizationUserRepository.findAll();
        assertThat(organizationUserList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOrganizationUsers() throws Exception {
        // Initialize the database
        organizationUserRepository.saveAndFlush(organizationUser);

        // Get all the organizationUserList
        restOrganizationUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organizationUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));
    }

    @Test
    @Transactional
    void getOrganizationUser() throws Exception {
        // Initialize the database
        organizationUserRepository.saveAndFlush(organizationUser);

        // Get the organizationUser
        restOrganizationUserMockMvc
            .perform(get(ENTITY_API_URL_ID, organizationUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(organizationUser.getId().intValue()))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingOrganizationUser() throws Exception {
        // Get the organizationUser
        restOrganizationUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrganizationUser() throws Exception {
        // Initialize the database
        organizationUserRepository.saveAndFlush(organizationUser);

        int databaseSizeBeforeUpdate = organizationUserRepository.findAll().size();
        organizationUserSearchRepository.save(organizationUser);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());

        // Update the organizationUser
        OrganizationUser updatedOrganizationUser = organizationUserRepository.findById(organizationUser.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrganizationUser are not directly saved in db
        em.detach(updatedOrganizationUser);
        updatedOrganizationUser.state(UPDATED_STATE).role(UPDATED_ROLE);

        restOrganizationUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrganizationUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrganizationUser))
            )
            .andExpect(status().isOk());

        // Validate the OrganizationUser in the database
        List<OrganizationUser> organizationUserList = organizationUserRepository.findAll();
        assertThat(organizationUserList).hasSize(databaseSizeBeforeUpdate);
        OrganizationUser testOrganizationUser = organizationUserList.get(organizationUserList.size() - 1);
        assertThat(testOrganizationUser.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testOrganizationUser.getRole()).isEqualTo(UPDATED_ROLE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<OrganizationUser> organizationUserSearchList = IterableUtils.toList(organizationUserSearchRepository.findAll());
                OrganizationUser testOrganizationUserSearch = organizationUserSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testOrganizationUserSearch.getState()).isEqualTo(UPDATED_STATE);
                assertThat(testOrganizationUserSearch.getRole()).isEqualTo(UPDATED_ROLE);
            });
    }

    @Test
    @Transactional
    void putNonExistingOrganizationUser() throws Exception {
        int databaseSizeBeforeUpdate = organizationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        organizationUser.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganizationUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, organizationUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(organizationUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrganizationUser in the database
        List<OrganizationUser> organizationUserList = organizationUserRepository.findAll();
        assertThat(organizationUserList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrganizationUser() throws Exception {
        int databaseSizeBeforeUpdate = organizationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        organizationUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizationUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(organizationUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrganizationUser in the database
        List<OrganizationUser> organizationUserList = organizationUserRepository.findAll();
        assertThat(organizationUserList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrganizationUser() throws Exception {
        int databaseSizeBeforeUpdate = organizationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        organizationUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizationUserMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organizationUser))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrganizationUser in the database
        List<OrganizationUser> organizationUserList = organizationUserRepository.findAll();
        assertThat(organizationUserList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOrganizationUserWithPatch() throws Exception {
        // Initialize the database
        organizationUserRepository.saveAndFlush(organizationUser);

        int databaseSizeBeforeUpdate = organizationUserRepository.findAll().size();

        // Update the organizationUser using partial update
        OrganizationUser partialUpdatedOrganizationUser = new OrganizationUser();
        partialUpdatedOrganizationUser.setId(organizationUser.getId());

        partialUpdatedOrganizationUser.state(UPDATED_STATE);

        restOrganizationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganizationUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganizationUser))
            )
            .andExpect(status().isOk());

        // Validate the OrganizationUser in the database
        List<OrganizationUser> organizationUserList = organizationUserRepository.findAll();
        assertThat(organizationUserList).hasSize(databaseSizeBeforeUpdate);
        OrganizationUser testOrganizationUser = organizationUserList.get(organizationUserList.size() - 1);
        assertThat(testOrganizationUser.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testOrganizationUser.getRole()).isEqualTo(DEFAULT_ROLE);
    }

    @Test
    @Transactional
    void fullUpdateOrganizationUserWithPatch() throws Exception {
        // Initialize the database
        organizationUserRepository.saveAndFlush(organizationUser);

        int databaseSizeBeforeUpdate = organizationUserRepository.findAll().size();

        // Update the organizationUser using partial update
        OrganizationUser partialUpdatedOrganizationUser = new OrganizationUser();
        partialUpdatedOrganizationUser.setId(organizationUser.getId());

        partialUpdatedOrganizationUser.state(UPDATED_STATE).role(UPDATED_ROLE);

        restOrganizationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganizationUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganizationUser))
            )
            .andExpect(status().isOk());

        // Validate the OrganizationUser in the database
        List<OrganizationUser> organizationUserList = organizationUserRepository.findAll();
        assertThat(organizationUserList).hasSize(databaseSizeBeforeUpdate);
        OrganizationUser testOrganizationUser = organizationUserList.get(organizationUserList.size() - 1);
        assertThat(testOrganizationUser.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testOrganizationUser.getRole()).isEqualTo(UPDATED_ROLE);
    }

    @Test
    @Transactional
    void patchNonExistingOrganizationUser() throws Exception {
        int databaseSizeBeforeUpdate = organizationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        organizationUser.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganizationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, organizationUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(organizationUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrganizationUser in the database
        List<OrganizationUser> organizationUserList = organizationUserRepository.findAll();
        assertThat(organizationUserList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrganizationUser() throws Exception {
        int databaseSizeBeforeUpdate = organizationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        organizationUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizationUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(organizationUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrganizationUser in the database
        List<OrganizationUser> organizationUserList = organizationUserRepository.findAll();
        assertThat(organizationUserList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrganizationUser() throws Exception {
        int databaseSizeBeforeUpdate = organizationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        organizationUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganizationUserMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(organizationUser))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrganizationUser in the database
        List<OrganizationUser> organizationUserList = organizationUserRepository.findAll();
        assertThat(organizationUserList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOrganizationUser() throws Exception {
        // Initialize the database
        organizationUserRepository.saveAndFlush(organizationUser);
        organizationUserRepository.save(organizationUser);
        organizationUserSearchRepository.save(organizationUser);

        int databaseSizeBeforeDelete = organizationUserRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the organizationUser
        restOrganizationUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, organizationUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrganizationUser> organizationUserList = organizationUserRepository.findAll();
        assertThat(organizationUserList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(organizationUserSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOrganizationUser() throws Exception {
        // Initialize the database
        organizationUser = organizationUserRepository.saveAndFlush(organizationUser);
        organizationUserSearchRepository.save(organizationUser);

        // Search the organizationUser
        restOrganizationUserMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + organizationUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organizationUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));
    }
}
