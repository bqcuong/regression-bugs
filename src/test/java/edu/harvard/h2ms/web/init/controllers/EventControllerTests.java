package edu.harvard.h2ms.web.init.controllers;

import edu.harvard.h2ms.H2MSRestAppInitializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@ContextConfiguration(classes = H2MSRestAppInitializer.class)
public class EventControllerTests extends AbstractJUnit4SpringContextTests {

    private static final Log log = LogFactory.getLog(EventControllerTests.class);

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
     * Tests the success of the /count/week endpoint.
     * The endpoint is used for retrieving all events grouped by a
     * specified timeframe (ie. week, month, year, quarter)
     */
    @Test
    public void test_Success_EventController_findEventCountByTimeframe() throws Exception {

        // Makes API calls and checks for success status
        MockHttpServletResponse result = mvc.perform(get("/events/count/week")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        // Asserts results are as expected
        Assert.isTrue(result.getContentAsString().equals("{\"12th (2018)\":1}"));

    }

}
