package org.crowdware.web.rest;

import org.crowdware.CrowdwareApp;
import org.crowdware.domain.App;
import org.crowdware.repository.AppRepository;
import org.crowdware.service.AppService;
import org.crowdware.repository.search.AppSearchRepository;

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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the AppResource REST controller.
 *
 * @see AppResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CrowdwareApp.class)
@WebAppConfiguration
@IntegrationTest
public class AppResourceIntTest {

    private static final String DEFAULT_NAME = "AAA";
    private static final String UPDATED_NAME = "BBB";
    private static final String DEFAULT_SOURCE = "AAAAA";
    private static final String UPDATED_SOURCE = "BBBBB";

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;

    @Inject
    private AppRepository appRepository;

    @Inject
    private AppService appService;

    @Inject
    private AppSearchRepository appSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAppMockMvc;

    private App app;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AppResource appResource = new AppResource();
        ReflectionTestUtils.setField(appResource, "appService", appService);
        this.restAppMockMvc = MockMvcBuilders.standaloneSetup(appResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        appSearchRepository.deleteAll();
        app = new App();
        app.setName(DEFAULT_NAME);
        app.setSource(DEFAULT_SOURCE);
        app.setVersion(DEFAULT_VERSION);
    }

    @Test
    @Transactional
    public void createApp() throws Exception {
        int databaseSizeBeforeCreate = appRepository.findAll().size();

        // Create the App

        restAppMockMvc.perform(post("/api/apps")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(app)))
                .andExpect(status().isCreated());

        // Validate the App in the database
        List<App> apps = appRepository.findAll();
        assertThat(apps).hasSize(databaseSizeBeforeCreate + 1);
        App testApp = apps.get(apps.size() - 1);
        assertThat(testApp.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testApp.getSource()).isEqualTo(DEFAULT_SOURCE);
        assertThat(testApp.getVersion()).isEqualTo(DEFAULT_VERSION);

        // Validate the App in ElasticSearch
        App appEs = appSearchRepository.findOne(testApp.getId());
        assertThat(appEs).isEqualToComparingFieldByField(testApp);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = appRepository.findAll().size();
        // set the field null
        app.setName(null);

        // Create the App, which fails.

        restAppMockMvc.perform(post("/api/apps")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(app)))
                .andExpect(status().isBadRequest());

        List<App> apps = appRepository.findAll();
        assertThat(apps).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllApps() throws Exception {
        // Initialize the database
        appRepository.saveAndFlush(app);

        // Get all the apps
        restAppMockMvc.perform(get("/api/apps?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(app.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE.toString())))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)));
    }

    @Test
    @Transactional
    public void getApp() throws Exception {
        // Initialize the database
        appRepository.saveAndFlush(app);

        // Get the app
        restAppMockMvc.perform(get("/api/apps/{id}", app.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(app.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.source").value(DEFAULT_SOURCE.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION));
    }

    @Test
    @Transactional
    public void getNonExistingApp() throws Exception {
        // Get the app
        restAppMockMvc.perform(get("/api/apps/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateApp() throws Exception {
        // Initialize the database
        appService.save(app);

        int databaseSizeBeforeUpdate = appRepository.findAll().size();

        // Update the app
        App updatedApp = new App();
        updatedApp.setId(app.getId());
        updatedApp.setName(UPDATED_NAME);
        updatedApp.setSource(UPDATED_SOURCE);
        updatedApp.setVersion(UPDATED_VERSION);

        restAppMockMvc.perform(put("/api/apps")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedApp)))
                .andExpect(status().isOk());

        // Validate the App in the database
        List<App> apps = appRepository.findAll();
        assertThat(apps).hasSize(databaseSizeBeforeUpdate);
        App testApp = apps.get(apps.size() - 1);
        assertThat(testApp.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testApp.getSource()).isEqualTo(UPDATED_SOURCE);
        assertThat(testApp.getVersion()).isEqualTo(UPDATED_VERSION);

        // Validate the App in ElasticSearch
        App appEs = appSearchRepository.findOne(testApp.getId());
        assertThat(appEs).isEqualToComparingFieldByField(testApp);
    }

    @Test
    @Transactional
    public void deleteApp() throws Exception {
        // Initialize the database
        appService.save(app);

        int databaseSizeBeforeDelete = appRepository.findAll().size();

        // Get the app
        restAppMockMvc.perform(delete("/api/apps/{id}", app.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean appExistsInEs = appSearchRepository.exists(app.getId());
        assertThat(appExistsInEs).isFalse();

        // Validate the database is empty
        List<App> apps = appRepository.findAll();
        assertThat(apps).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchApp() throws Exception {
        // Initialize the database
        appService.save(app);

        // Search the app
        restAppMockMvc.perform(get("/api/_search/apps?query=id:" + app.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(app.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)));
    }
}
