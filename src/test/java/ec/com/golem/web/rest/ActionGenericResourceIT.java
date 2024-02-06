package ec.com.golem.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ec.com.golem.IntegrationTest;
import ec.com.golem.domain.ActionGeneric;
import ec.com.golem.repository.ActionGenericRepository;
import ec.com.golem.repository.search.ActionGenericSearchRepository;
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
 * Integration tests for the {@link ActionGenericResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ActionGenericResourceIT {

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/action-generics";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/action-generics/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActionGenericRepository actionGenericRepository;

    @Autowired
    private ActionGenericSearchRepository actionGenericSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActionGenericMockMvc;

    private ActionGeneric actionGeneric;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ActionGeneric createEntity(EntityManager em) {
        ActionGeneric actionGeneric = new ActionGeneric().message(DEFAULT_MESSAGE);
        return actionGeneric;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ActionGeneric createUpdatedEntity(EntityManager em) {
        ActionGeneric actionGeneric = new ActionGeneric().message(UPDATED_MESSAGE);
        return actionGeneric;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        actionGenericSearchRepository.deleteAll();
        assertThat(actionGenericSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        actionGeneric = createEntity(em);
    }

    @Test
    @Transactional
    void createActionGeneric() throws Exception {
        int databaseSizeBeforeCreate = actionGenericRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        // Create the ActionGeneric
        restActionGenericMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actionGeneric)))
            .andExpect(status().isCreated());

        // Validate the ActionGeneric in the database
        List<ActionGeneric> actionGenericList = actionGenericRepository.findAll();
        assertThat(actionGenericList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ActionGeneric testActionGeneric = actionGenericList.get(actionGenericList.size() - 1);
        assertThat(testActionGeneric.getMessage()).isEqualTo(DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    void createActionGenericWithExistingId() throws Exception {
        // Create the ActionGeneric with an existing ID
        actionGeneric.setId(1L);

        int databaseSizeBeforeCreate = actionGenericRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restActionGenericMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actionGeneric)))
            .andExpect(status().isBadRequest());

        // Validate the ActionGeneric in the database
        List<ActionGeneric> actionGenericList = actionGenericRepository.findAll();
        assertThat(actionGenericList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllActionGenerics() throws Exception {
        // Initialize the database
        actionGenericRepository.saveAndFlush(actionGeneric);

        // Get all the actionGenericList
        restActionGenericMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actionGeneric.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)));
    }

    @Test
    @Transactional
    void getActionGeneric() throws Exception {
        // Initialize the database
        actionGenericRepository.saveAndFlush(actionGeneric);

        // Get the actionGeneric
        restActionGenericMockMvc
            .perform(get(ENTITY_API_URL_ID, actionGeneric.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(actionGeneric.getId().intValue()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE));
    }

    @Test
    @Transactional
    void getNonExistingActionGeneric() throws Exception {
        // Get the actionGeneric
        restActionGenericMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingActionGeneric() throws Exception {
        // Initialize the database
        actionGenericRepository.saveAndFlush(actionGeneric);

        int databaseSizeBeforeUpdate = actionGenericRepository.findAll().size();
        actionGenericSearchRepository.save(actionGeneric);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());

        // Update the actionGeneric
        ActionGeneric updatedActionGeneric = actionGenericRepository.findById(actionGeneric.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedActionGeneric are not directly saved in db
        em.detach(updatedActionGeneric);
        updatedActionGeneric.message(UPDATED_MESSAGE);

        restActionGenericMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedActionGeneric.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedActionGeneric))
            )
            .andExpect(status().isOk());

        // Validate the ActionGeneric in the database
        List<ActionGeneric> actionGenericList = actionGenericRepository.findAll();
        assertThat(actionGenericList).hasSize(databaseSizeBeforeUpdate);
        ActionGeneric testActionGeneric = actionGenericList.get(actionGenericList.size() - 1);
        assertThat(testActionGeneric.getMessage()).isEqualTo(UPDATED_MESSAGE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ActionGeneric> actionGenericSearchList = IterableUtils.toList(actionGenericSearchRepository.findAll());
                ActionGeneric testActionGenericSearch = actionGenericSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testActionGenericSearch.getMessage()).isEqualTo(UPDATED_MESSAGE);
            });
    }

    @Test
    @Transactional
    void putNonExistingActionGeneric() throws Exception {
        int databaseSizeBeforeUpdate = actionGenericRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        actionGeneric.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActionGenericMockMvc
            .perform(
                put(ENTITY_API_URL_ID, actionGeneric.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionGeneric))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionGeneric in the database
        List<ActionGeneric> actionGenericList = actionGenericRepository.findAll();
        assertThat(actionGenericList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchActionGeneric() throws Exception {
        int databaseSizeBeforeUpdate = actionGenericRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        actionGeneric.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionGenericMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actionGeneric))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionGeneric in the database
        List<ActionGeneric> actionGenericList = actionGenericRepository.findAll();
        assertThat(actionGenericList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamActionGeneric() throws Exception {
        int databaseSizeBeforeUpdate = actionGenericRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        actionGeneric.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionGenericMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actionGeneric)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ActionGeneric in the database
        List<ActionGeneric> actionGenericList = actionGenericRepository.findAll();
        assertThat(actionGenericList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateActionGenericWithPatch() throws Exception {
        // Initialize the database
        actionGenericRepository.saveAndFlush(actionGeneric);

        int databaseSizeBeforeUpdate = actionGenericRepository.findAll().size();

        // Update the actionGeneric using partial update
        ActionGeneric partialUpdatedActionGeneric = new ActionGeneric();
        partialUpdatedActionGeneric.setId(actionGeneric.getId());

        partialUpdatedActionGeneric.message(UPDATED_MESSAGE);

        restActionGenericMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActionGeneric.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActionGeneric))
            )
            .andExpect(status().isOk());

        // Validate the ActionGeneric in the database
        List<ActionGeneric> actionGenericList = actionGenericRepository.findAll();
        assertThat(actionGenericList).hasSize(databaseSizeBeforeUpdate);
        ActionGeneric testActionGeneric = actionGenericList.get(actionGenericList.size() - 1);
        assertThat(testActionGeneric.getMessage()).isEqualTo(UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void fullUpdateActionGenericWithPatch() throws Exception {
        // Initialize the database
        actionGenericRepository.saveAndFlush(actionGeneric);

        int databaseSizeBeforeUpdate = actionGenericRepository.findAll().size();

        // Update the actionGeneric using partial update
        ActionGeneric partialUpdatedActionGeneric = new ActionGeneric();
        partialUpdatedActionGeneric.setId(actionGeneric.getId());

        partialUpdatedActionGeneric.message(UPDATED_MESSAGE);

        restActionGenericMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActionGeneric.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActionGeneric))
            )
            .andExpect(status().isOk());

        // Validate the ActionGeneric in the database
        List<ActionGeneric> actionGenericList = actionGenericRepository.findAll();
        assertThat(actionGenericList).hasSize(databaseSizeBeforeUpdate);
        ActionGeneric testActionGeneric = actionGenericList.get(actionGenericList.size() - 1);
        assertThat(testActionGeneric.getMessage()).isEqualTo(UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void patchNonExistingActionGeneric() throws Exception {
        int databaseSizeBeforeUpdate = actionGenericRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        actionGeneric.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActionGenericMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, actionGeneric.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actionGeneric))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionGeneric in the database
        List<ActionGeneric> actionGenericList = actionGenericRepository.findAll();
        assertThat(actionGenericList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchActionGeneric() throws Exception {
        int databaseSizeBeforeUpdate = actionGenericRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        actionGeneric.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionGenericMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actionGeneric))
            )
            .andExpect(status().isBadRequest());

        // Validate the ActionGeneric in the database
        List<ActionGeneric> actionGenericList = actionGenericRepository.findAll();
        assertThat(actionGenericList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamActionGeneric() throws Exception {
        int databaseSizeBeforeUpdate = actionGenericRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        actionGeneric.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActionGenericMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(actionGeneric))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ActionGeneric in the database
        List<ActionGeneric> actionGenericList = actionGenericRepository.findAll();
        assertThat(actionGenericList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteActionGeneric() throws Exception {
        // Initialize the database
        actionGenericRepository.saveAndFlush(actionGeneric);
        actionGenericRepository.save(actionGeneric);
        actionGenericSearchRepository.save(actionGeneric);

        int databaseSizeBeforeDelete = actionGenericRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the actionGeneric
        restActionGenericMockMvc
            .perform(delete(ENTITY_API_URL_ID, actionGeneric.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ActionGeneric> actionGenericList = actionGenericRepository.findAll();
        assertThat(actionGenericList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(actionGenericSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchActionGeneric() throws Exception {
        // Initialize the database
        actionGeneric = actionGenericRepository.saveAndFlush(actionGeneric);
        actionGenericSearchRepository.save(actionGeneric);

        // Search the actionGeneric
        restActionGenericMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + actionGeneric.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actionGeneric.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)));
    }
}
