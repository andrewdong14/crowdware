package org.crowdware.web.rest;

import org.crowdware.CrowdwareApp;
import org.crowdware.domain.JobAttribute;
import org.crowdware.repository.JobAttributeRepository;
import org.crowdware.repository.search.JobAttributeSearchRepository;

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
 * Test class for the JobAttributeResource REST controller.
 *
 * @see JobAttributeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CrowdwareApp.class)
@WebAppConfiguration
@IntegrationTest
public class JobAttributeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_VALUE = "AAAAA";
    private static final String UPDATED_VALUE = "BBBBB";

    @Inject
    private JobAttributeRepository jobAttributeRepository;

    @Inject
    private JobAttributeSearchRepository jobAttributeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restJobAttributeMockMvc;

    private JobAttribute jobAttribute;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        JobAttributeResource jobAttributeResource = new JobAttributeResource();
        ReflectionTestUtils.setField(jobAttributeResource, "jobAttributeSearchRepository", jobAttributeSearchRepository);
        ReflectionTestUtils.setField(jobAttributeResource, "jobAttributeRepository", jobAttributeRepository);
        this.restJobAttributeMockMvc = MockMvcBuilders.standaloneSetup(jobAttributeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        jobAttributeSearchRepository.deleteAll();
        jobAttribute = new JobAttribute();
        jobAttribute.setName(DEFAULT_NAME);
        jobAttribute.setValue(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createJobAttribute() throws Exception {
        int databaseSizeBeforeCreate = jobAttributeRepository.findAll().size();

        // Create the JobAttribute

        restJobAttributeMockMvc.perform(post("/api/job-attributes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(jobAttribute)))
                .andExpect(status().isCreated());

        // Validate the JobAttribute in the database
        List<JobAttribute> jobAttributes = jobAttributeRepository.findAll();
        assertThat(jobAttributes).hasSize(databaseSizeBeforeCreate + 1);
        JobAttribute testJobAttribute = jobAttributes.get(jobAttributes.size() - 1);
        assertThat(testJobAttribute.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testJobAttribute.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the JobAttribute in ElasticSearch
        JobAttribute jobAttributeEs = jobAttributeSearchRepository.findOne(testJobAttribute.getId());
        assertThat(jobAttributeEs).isEqualToComparingFieldByField(testJobAttribute);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobAttributeRepository.findAll().size();
        // set the field null
        jobAttribute.setName(null);

        // Create the JobAttribute, which fails.

        restJobAttributeMockMvc.perform(post("/api/job-attributes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(jobAttribute)))
                .andExpect(status().isBadRequest());

        List<JobAttribute> jobAttributes = jobAttributeRepository.findAll();
        assertThat(jobAttributes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllJobAttributes() throws Exception {
        // Initialize the database
        jobAttributeRepository.saveAndFlush(jobAttribute);

        // Get all the jobAttributes
        restJobAttributeMockMvc.perform(get("/api/job-attributes?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(jobAttribute.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }

    @Test
    @Transactional
    public void getJobAttribute() throws Exception {
        // Initialize the database
        jobAttributeRepository.saveAndFlush(jobAttribute);

        // Get the jobAttribute
        restJobAttributeMockMvc.perform(get("/api/job-attributes/{id}", jobAttribute.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(jobAttribute.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingJobAttribute() throws Exception {
        // Get the jobAttribute
        restJobAttributeMockMvc.perform(get("/api/job-attributes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJobAttribute() throws Exception {
        // Initialize the database
        jobAttributeRepository.saveAndFlush(jobAttribute);
        jobAttributeSearchRepository.save(jobAttribute);
        int databaseSizeBeforeUpdate = jobAttributeRepository.findAll().size();

        // Update the jobAttribute
        JobAttribute updatedJobAttribute = new JobAttribute();
        updatedJobAttribute.setId(jobAttribute.getId());
        updatedJobAttribute.setName(UPDATED_NAME);
        updatedJobAttribute.setValue(UPDATED_VALUE);

        restJobAttributeMockMvc.perform(put("/api/job-attributes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedJobAttribute)))
                .andExpect(status().isOk());

        // Validate the JobAttribute in the database
        List<JobAttribute> jobAttributes = jobAttributeRepository.findAll();
        assertThat(jobAttributes).hasSize(databaseSizeBeforeUpdate);
        JobAttribute testJobAttribute = jobAttributes.get(jobAttributes.size() - 1);
        assertThat(testJobAttribute.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testJobAttribute.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the JobAttribute in ElasticSearch
        JobAttribute jobAttributeEs = jobAttributeSearchRepository.findOne(testJobAttribute.getId());
        assertThat(jobAttributeEs).isEqualToComparingFieldByField(testJobAttribute);
    }

    @Test
    @Transactional
    public void deleteJobAttribute() throws Exception {
        // Initialize the database
        jobAttributeRepository.saveAndFlush(jobAttribute);
        jobAttributeSearchRepository.save(jobAttribute);
        int databaseSizeBeforeDelete = jobAttributeRepository.findAll().size();

        // Get the jobAttribute
        restJobAttributeMockMvc.perform(delete("/api/job-attributes/{id}", jobAttribute.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean jobAttributeExistsInEs = jobAttributeSearchRepository.exists(jobAttribute.getId());
        assertThat(jobAttributeExistsInEs).isFalse();

        // Validate the database is empty
        List<JobAttribute> jobAttributes = jobAttributeRepository.findAll();
        assertThat(jobAttributes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchJobAttribute() throws Exception {
        // Initialize the database
        jobAttributeRepository.saveAndFlush(jobAttribute);
        jobAttributeSearchRepository.save(jobAttribute);

        // Search the jobAttribute
        restJobAttributeMockMvc.perform(get("/api/_search/job-attributes?query=id:" + jobAttribute.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobAttribute.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }
}
