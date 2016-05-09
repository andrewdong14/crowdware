package org.crowdware.web.rest;

import org.crowdware.CrowdwareApp;
import org.crowdware.domain.CrowdApp;
import org.crowdware.repository.CrowdAppRepository;
import org.crowdware.service.CrowdAppService;
import org.crowdware.repository.search.CrowdAppSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the CrowdAppResource REST controller.
 *
 * @see CrowdAppResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CrowdwareApp.class)
@WebAppConfiguration
@IntegrationTest
public class CrowdAppResourceIntTest {

    private static final String DEFAULT_NAME = "AAA";
    private static final String UPDATED_NAME = "BBB";

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;

    private static final String DEFAULT_SOURCE = "AAA";
    private static final String UPDATED_SOURCE = "BBB";

    @Inject
    private CrowdAppRepository crowdAppRepository;

    @Inject
    private CrowdAppService crowdAppService;

    @Inject
    private CrowdAppSearchRepository crowdAppSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCrowdAppMockMvc;

    private CrowdApp crowdApp;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CrowdAppResource crowdAppResource = new CrowdAppResource();
        ReflectionTestUtils.setField(crowdAppResource, "crowdAppService", crowdAppService);
        this.restCrowdAppMockMvc = MockMvcBuilders.standaloneSetup(crowdAppResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        crowdAppSearchRepository.deleteAll();
        crowdApp = new CrowdApp();
        crowdApp.setName(DEFAULT_NAME);
        crowdApp.setVersion(DEFAULT_VERSION);
        crowdApp.setSource(DEFAULT_SOURCE);
    }

    @Test
    @Transactional
    public void createCrowdApp() throws Exception {
        int databaseSizeBeforeCreate = crowdAppRepository.findAll().size();

        // Create the CrowdApp

        restCrowdAppMockMvc.perform(post("/api/crowd-apps")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(crowdApp)))
                .andExpect(status().isCreated());

        // Validate the CrowdApp in the database
        List<CrowdApp> crowdApps = crowdAppRepository.findAll();
        assertThat(crowdApps).hasSize(databaseSizeBeforeCreate + 1);
        CrowdApp testCrowdApp = crowdApps.get(crowdApps.size() - 1);
        assertThat(testCrowdApp.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCrowdApp.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testCrowdApp.getSource()).isEqualTo(DEFAULT_SOURCE);

        // Validate the CrowdApp in ElasticSearch
        CrowdApp crowdAppEs = crowdAppSearchRepository.findOne(testCrowdApp.getId());
        assertThat(crowdAppEs).isEqualToComparingFieldByField(testCrowdApp);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = crowdAppRepository.findAll().size();
        // set the field null
        crowdApp.setName(null);

        // Create the CrowdApp, which fails.

        restCrowdAppMockMvc.perform(post("/api/crowd-apps")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(crowdApp)))
                .andExpect(status().isBadRequest());

        List<CrowdApp> crowdApps = crowdAppRepository.findAll();
        assertThat(crowdApps).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCrowdApps() throws Exception {
        // Initialize the database
        crowdAppRepository.saveAndFlush(crowdApp);

        // Get all the crowdApps
        restCrowdAppMockMvc.perform(get("/api/crowd-apps?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(crowdApp.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
                .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE.toString())));
    }

    @Test
    @Transactional
    public void getCrowdApp() throws Exception {
        // Initialize the database
        crowdAppRepository.saveAndFlush(crowdApp);

        // Get the crowdApp
        restCrowdAppMockMvc.perform(get("/api/crowd-apps/{id}", crowdApp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(crowdApp.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.source").value(DEFAULT_SOURCE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCrowdApp() throws Exception {
        // Get the crowdApp
        restCrowdAppMockMvc.perform(get("/api/crowd-apps/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCrowdApp() throws Exception {
        // Initialize the database
        crowdAppService.save(crowdApp);

        int databaseSizeBeforeUpdate = crowdAppRepository.findAll().size();

        // Update the crowdApp
        CrowdApp updatedCrowdApp = new CrowdApp();
        updatedCrowdApp.setId(crowdApp.getId());
        updatedCrowdApp.setName(UPDATED_NAME);
        updatedCrowdApp.setVersion(UPDATED_VERSION);
        updatedCrowdApp.setSource(UPDATED_SOURCE);

        restCrowdAppMockMvc.perform(put("/api/crowd-apps")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCrowdApp)))
                .andExpect(status().isOk());

        // Validate the CrowdApp in the database
        List<CrowdApp> crowdApps = crowdAppRepository.findAll();
        assertThat(crowdApps).hasSize(databaseSizeBeforeUpdate);
        CrowdApp testCrowdApp = crowdApps.get(crowdApps.size() - 1);
        assertThat(testCrowdApp.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCrowdApp.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testCrowdApp.getSource()).isEqualTo(UPDATED_SOURCE);

        // Validate the CrowdApp in ElasticSearch
        CrowdApp crowdAppEs = crowdAppSearchRepository.findOne(testCrowdApp.getId());
        assertThat(crowdAppEs).isEqualToComparingFieldByField(testCrowdApp);
    }

    @Test
    @Transactional
    public void deleteCrowdApp() throws Exception {
        // Initialize the database
        crowdAppService.save(crowdApp);

        int databaseSizeBeforeDelete = crowdAppRepository.findAll().size();

        // Get the crowdApp
        restCrowdAppMockMvc.perform(delete("/api/crowd-apps/{id}", crowdApp.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean crowdAppExistsInEs = crowdAppSearchRepository.exists(crowdApp.getId());
        assertThat(crowdAppExistsInEs).isFalse();

        // Validate the database is empty
        List<CrowdApp> crowdApps = crowdAppRepository.findAll();
        assertThat(crowdApps).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCrowdApp() throws Exception {
        // Initialize the database
        crowdAppService.save(crowdApp);

        // Search the crowdApp
        restCrowdAppMockMvc.perform(get("/api/_search/crowd-apps?query=id:" + crowdApp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(crowdApp.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE.toString())));
    }
}
