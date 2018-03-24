package edu.harvard.h2ms.controllers;

import edu.harvard.h2ms.H2MSRestAppInitializer;
import edu.harvard.h2ms.domain.core.User;
import edu.harvard.h2ms.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class EventControllerTests {

    private static final Log log = LogFactory.getLog(EventControllerTests.class);

    static final String EMAIL = "jqadams@h2ms.org";
    static final String PASSWORD = "password";
    static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    /**
     * Setup prior to running unit tests
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(springSecurityFilterChain)
                .build();
        User user = new User("John", "Quincy", "Adams", EMAIL, PASSWORD);
        userRepository.save(user);
        //userRepository.findOneByEmail(EMAIL)).thenReturn(user);
    }

    /**
     * Unit Test #1
     * Tests the success of the /count/week endpoint.
     * The endpoint is used for retrieving all events grouped by a
     * specified timeframe (ie. week, month, year, quarter)
     */
    @Test
    public void test_Success_EventController_findEventCountByTimeframe() throws Exception {

        final String accessToken = obtainAccessToken("jqadams@h2ms.org", "password");

        // Makes API calls and checks for success status
        MockHttpServletResponse result = mvc.perform(get("/events/count/week")
                .header("Authorization", "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Assert.isTrue(result.getContentAsString().contains("12th (2018)"));

    }

    private String obtainAccessToken(String username, String password) throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);

        ResultActions result =
                mvc.perform(
                        post("/oauth/token")
                                .params(params)
                                .with(httpBasic("h2ms", "secret"))
                                .accept("application/json"))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(CONTENT_TYPE));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

}
