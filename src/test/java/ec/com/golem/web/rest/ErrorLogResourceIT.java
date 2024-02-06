package ec.com.golem.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ec.com.golem.IntegrationTest;
import ec.com.golem.domain.ErrorLog;
import ec.com.golem.repository.ErrorLogRepository;
import ec.com.golem.repository.search.ErrorLogSearchRepository;
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
 * Integration tests for the {@link ErrorLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ErrorLogResourceIT {

    private static final String DEFAULT_CLASS_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CLASS_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_LINE = 1L;
    private static final Long UPDATED_LINE = 2L;

    private static final String DEFAULT_ERROR = "AAAAAAAAAA";
    private static final String UPDATED_ERROR = "BBBBBBBBBB";

    private static final String DEFAULT_ERROY_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ERROY_TYPE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_TIMESTAMP = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TIMESTAMP = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/error-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/error-logs/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ErrorLogRepository errorLogRepository;

    @Autowired
    private ErrorLogSearchRepository errorLogSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restErrorLogMockMvc;

    private ErrorLog errorLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ErrorLog createEntity(EntityManager em) {
        ErrorLog errorLog = new ErrorLog()
            .className(DEFAULT_CLASS_NAME)
            .line(DEFAULT_LINE)
            .error(DEFAULT_ERROR)
            .erroyType(DEFAULT_ERROY_TYPE)
            .timestamp(DEFAULT_TIMESTAMP);
        return errorLog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ErrorLog createUpdatedEntity(EntityManager em) {
        ErrorLog errorLog = new ErrorLog()
            .className(UPDATED_CLASS_NAME)
            .line(UPDATED_LINE)
            .error(UPDATED_ERROR)
            .erroyType(UPDATED_ERROY_TYPE)
            .timestamp(UPDATED_TIMESTAMP);
        return errorLog;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        errorLogSearchRepository.deleteAll();
        assertThat(errorLogSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        errorLog = createEntity(em);
    }

    @Test
    @Transactional
    void createErrorLog() throws Exception {
        int databaseSizeBeforeCreate = errorLogRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        // Create the ErrorLog
        restErrorLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(errorLog)))
            .andExpect(status().isCreated());

        // Validate the ErrorLog in the database
        List<ErrorLog> errorLogList = errorLogRepository.findAll();
        assertThat(errorLogList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ErrorLog testErrorLog = errorLogList.get(errorLogList.size() - 1);
        assertThat(testErrorLog.getClassName()).isEqualTo(DEFAULT_CLASS_NAME);
        assertThat(testErrorLog.getLine()).isEqualTo(DEFAULT_LINE);
        assertThat(testErrorLog.getError()).isEqualTo(DEFAULT_ERROR);
        assertThat(testErrorLog.getErroyType()).isEqualTo(DEFAULT_ERROY_TYPE);
        assertThat(testErrorLog.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
    }

    @Test
    @Transactional
    void createErrorLogWithExistingId() throws Exception {
        // Create the ErrorLog with an existing ID
        errorLog.setId(1L);

        int databaseSizeBeforeCreate = errorLogRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(errorLogSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restErrorLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(errorLog)))
            .andExpect(status().isBadRequest());

        // Validate the ErrorLog in the database
        List<ErrorLog> errorLogList = errorLogRepository.findAll();
        assertThat(errorLogList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllErrorLogs() throws Exception {
        // Initialize the database
        errorLogRepository.saveAndFlush(errorLog);

        // Get all the errorLogList
        restErrorLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(errorLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].className").value(hasItem(DEFAULT_CLASS_NAME)))
            .andExpect(jsonPath("$.[*].line").value(hasItem(DEFAULT_LINE.intValue())))
            .andExpect(jsonPath("$.[*].error").value(hasItem(DEFAULT_ERROR)))
            .andExpect(jsonPath("$.[*].erroyType").value(hasItem(DEFAULT_ERROY_TYPE)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())));
    }

    @Test
    @Transactional
    void getErrorLog() throws Exception {
        // Initialize the database
        errorLogRepository.saveAndFlush(errorLog);

        // Get the errorLog
        restErrorLogMockMvc
            .perform(get(ENTITY_API_URL_ID, errorLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(errorLog.getId().intValue()))
            .andExpect(jsonPath("$.className").value(DEFAULT_CLASS_NAME))
            .andExpect(jsonPath("$.line").value(DEFAULT_LINE.intValue()))
            .andExpect(jsonPath("$.error").value(DEFAULT_ERROR))
            .andExpect(jsonPath("$.erroyType").value(DEFAULT_ERROY_TYPE))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()));
    }

    @Test
    @Transactional
    void getNonExistingErrorLog() throws Exception {
        // Get the errorLog
        restErrorLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingErrorLog() throws Exception {
        // Initialize the database
        errorLogRepository.saveAndFlush(errorLog);

        int databaseSizeBeforeUpdate = errorLogRepository.findAll().size();
        errorLogSearchRepository.save(errorLog);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(errorLogSearchRepository.findAll());

        // Update the errorLog
        ErrorLog updatedErrorLog = errorLogRepository.findById(errorLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedErrorLog are not directly saved in db
        em.detach(updatedErrorLog);
        updatedErrorLog
            .className(UPDATED_CLASS_NAME)
            .line(UPDATED_LINE)
            .error(UPDATED_ERROR)
            .erroyType(UPDATED_ERROY_TYPE)
            .timestamp(UPDATED_TIMESTAMP);

        restErrorLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedErrorLog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedErrorLog))
            )
            .andExpect(status().isOk());

        // Validate the ErrorLog in the database
        List<ErrorLog> errorLogList = errorLogRepository.findAll();
        assertThat(errorLogList).hasSize(databaseSizeBeforeUpdate);
        ErrorLog testErrorLog = errorLogList.get(errorLogList.size() - 1);
        assertThat(testErrorLog.getClassName()).isEqualTo(UPDATED_CLASS_NAME);
        assertThat(testErrorLog.getLine()).isEqualTo(UPDATED_LINE);
        assertThat(testErrorLog.getError()).isEqualTo(UPDATED_ERROR);
        assertThat(testErrorLog.getErroyType()).isEqualTo(UPDATED_ERROY_TYPE);
        assertThat(testErrorLog.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ErrorLog> errorLogSearchList = IterableUtils.toList(errorLogSearchRepository.findAll());
                ErrorLog testErrorLogSearch = errorLogSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testErrorLogSearch.getClassName()).isEqualTo(UPDATED_CLASS_NAME);
                assertThat(testErrorLogSearch.getLine()).isEqualTo(UPDATED_LINE);
                assertThat(testErrorLogSearch.getError()).isEqualTo(UPDATED_ERROR);
                assertThat(testErrorLogSearch.getErroyType()).isEqualTo(UPDATED_ERROY_TYPE);
                assertThat(testErrorLogSearch.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
            });
    }

    @Test
    @Transactional
    void putNonExistingErrorLog() throws Exception {
        int databaseSizeBeforeUpdate = errorLogRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        errorLog.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restErrorLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, errorLog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(errorLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the ErrorLog in the database
        List<ErrorLog> errorLogList = errorLogRepository.findAll();
        assertThat(errorLogList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchErrorLog() throws Exception {
        int databaseSizeBeforeUpdate = errorLogRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        errorLog.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restErrorLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(errorLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the ErrorLog in the database
        List<ErrorLog> errorLogList = errorLogRepository.findAll();
        assertThat(errorLogList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamErrorLog() throws Exception {
        int databaseSizeBeforeUpdate = errorLogRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        errorLog.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restErrorLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(errorLog)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ErrorLog in the database
        List<ErrorLog> errorLogList = errorLogRepository.findAll();
        assertThat(errorLogList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateErrorLogWithPatch() throws Exception {
        // Initialize the database
        errorLogRepository.saveAndFlush(errorLog);

        int databaseSizeBeforeUpdate = errorLogRepository.findAll().size();

        // Update the errorLog using partial update
        ErrorLog partialUpdatedErrorLog = new ErrorLog();
        partialUpdatedErrorLog.setId(errorLog.getId());

        partialUpdatedErrorLog.line(UPDATED_LINE).error(UPDATED_ERROR).erroyType(UPDATED_ERROY_TYPE);

        restErrorLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedErrorLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedErrorLog))
            )
            .andExpect(status().isOk());

        // Validate the ErrorLog in the database
        List<ErrorLog> errorLogList = errorLogRepository.findAll();
        assertThat(errorLogList).hasSize(databaseSizeBeforeUpdate);
        ErrorLog testErrorLog = errorLogList.get(errorLogList.size() - 1);
        assertThat(testErrorLog.getClassName()).isEqualTo(DEFAULT_CLASS_NAME);
        assertThat(testErrorLog.getLine()).isEqualTo(UPDATED_LINE);
        assertThat(testErrorLog.getError()).isEqualTo(UPDATED_ERROR);
        assertThat(testErrorLog.getErroyType()).isEqualTo(UPDATED_ERROY_TYPE);
        assertThat(testErrorLog.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);
    }

    @Test
    @Transactional
    void fullUpdateErrorLogWithPatch() throws Exception {
        // Initialize the database
        errorLogRepository.saveAndFlush(errorLog);

        int databaseSizeBeforeUpdate = errorLogRepository.findAll().size();

        // Update the errorLog using partial update
        ErrorLog partialUpdatedErrorLog = new ErrorLog();
        partialUpdatedErrorLog.setId(errorLog.getId());

        partialUpdatedErrorLog
            .className(UPDATED_CLASS_NAME)
            .line(UPDATED_LINE)
            .error(UPDATED_ERROR)
            .erroyType(UPDATED_ERROY_TYPE)
            .timestamp(UPDATED_TIMESTAMP);

        restErrorLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedErrorLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedErrorLog))
            )
            .andExpect(status().isOk());

        // Validate the ErrorLog in the database
        List<ErrorLog> errorLogList = errorLogRepository.findAll();
        assertThat(errorLogList).hasSize(databaseSizeBeforeUpdate);
        ErrorLog testErrorLog = errorLogList.get(errorLogList.size() - 1);
        assertThat(testErrorLog.getClassName()).isEqualTo(UPDATED_CLASS_NAME);
        assertThat(testErrorLog.getLine()).isEqualTo(UPDATED_LINE);
        assertThat(testErrorLog.getError()).isEqualTo(UPDATED_ERROR);
        assertThat(testErrorLog.getErroyType()).isEqualTo(UPDATED_ERROY_TYPE);
        assertThat(testErrorLog.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);
    }

    @Test
    @Transactional
    void patchNonExistingErrorLog() throws Exception {
        int databaseSizeBeforeUpdate = errorLogRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        errorLog.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restErrorLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, errorLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(errorLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the ErrorLog in the database
        List<ErrorLog> errorLogList = errorLogRepository.findAll();
        assertThat(errorLogList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchErrorLog() throws Exception {
        int databaseSizeBeforeUpdate = errorLogRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        errorLog.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restErrorLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(errorLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the ErrorLog in the database
        List<ErrorLog> errorLogList = errorLogRepository.findAll();
        assertThat(errorLogList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamErrorLog() throws Exception {
        int databaseSizeBeforeUpdate = errorLogRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        errorLog.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restErrorLogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(errorLog)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ErrorLog in the database
        List<ErrorLog> errorLogList = errorLogRepository.findAll();
        assertThat(errorLogList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteErrorLog() throws Exception {
        // Initialize the database
        errorLogRepository.saveAndFlush(errorLog);
        errorLogRepository.save(errorLog);
        errorLogSearchRepository.save(errorLog);

        int databaseSizeBeforeDelete = errorLogRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the errorLog
        restErrorLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, errorLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ErrorLog> errorLogList = errorLogRepository.findAll();
        assertThat(errorLogList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(errorLogSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchErrorLog() throws Exception {
        // Initialize the database
        errorLog = errorLogRepository.saveAndFlush(errorLog);
        errorLogSearchRepository.save(errorLog);

        // Search the errorLog
        restErrorLogMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + errorLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(errorLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].className").value(hasItem(DEFAULT_CLASS_NAME)))
            .andExpect(jsonPath("$.[*].line").value(hasItem(DEFAULT_LINE.intValue())))
            .andExpect(jsonPath("$.[*].error").value(hasItem(DEFAULT_ERROR)))
            .andExpect(jsonPath("$.[*].erroyType").value(hasItem(DEFAULT_ERROY_TYPE)))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())));
    }
}
