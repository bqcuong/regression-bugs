package edu.harvard.h2ms.controllers;

import edu.harvard.h2ms.H2MSRestAppInitializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@ContextConfiguration(classes = H2MSRestAppInitializer.class)
public class UserControllerTests extends AbstractJUnit4SpringContextTests {

    private static final Log log = LogFactory.getLog(UserControllerTests.class);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    /**
     * Setup prior to running unit tests
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * Unit Test #1
     * Tests the success of the /avgWashed/ endpoint. The endpoint
     * is used to retrieve the average of hand washing compliance per user type.
     */
    @Test
    public void test_Success_UserController_findAvgWashCompliance() throws Exception {

        // Makes API calls and checks for success status
        mvc.perform(get("/users/avgWashed/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

}
