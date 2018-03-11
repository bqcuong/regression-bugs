package edu.harvard.h2ms.web.init;

import edu.harvard.h2ms.domain.core.Event;
import edu.harvard.h2ms.service.EventService;
import edu.harvard.h2ms.web.controller.EventController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.LinkedList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class EventControllerUnitTests {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(eventController)
                .build();
    }

    /**
     * Test Find Event By Id Rest Endpoint
     * @throws Exception
     */
    @Test
    public void test_endPoint_findEventById() throws Exception {
        Event event = new Event();
        event.setId(1L);
        event.setHandWashType("Soap");
        event.setObservationType("Hand Wash");
        event.setObservee("John Smith");
        event.setObserver("Jane Doe");
        event.setRelativeMoment("Before Entering Room");
        //MOCK ALERT: Return mocked result set on find
        when(eventService.findById(1L)).thenReturn(event);
        // Invokes method to test
        Event response = eventService.findById(1L);
        //MOCK ALERT: verify the method was called
        verify(eventService).findById(1L);
    }

    /**
     * Test Find All Events Rest Endpoint
     * @throws Exception
     */
    @Test
    public void test_endPoint_findAllEvents() throws Exception {
        List events = new LinkedList();
        Event event_one = new Event();
        event_one.setId(1L);
        event_one.setHandWashType("Soap");
        event_one.setObservationType("Hand Wash");
        event_one.setObservee("John Smith");
        event_one.setObserver("Jane Doe");
        event_one.setRelativeMoment("Before Entering Room");
        events.add(event_one);
        Event event_two = new Event();
        event_two.setId(2L);
        event_two.setHandWashType("Sanitizer");
        event_two.setObservationType("Hand Wash");
        event_two.setObservee("Arya Stark");
        event_two.setObserver("Jon Snow");
        event_two.setRelativeMoment("Before Exiting the North");
        events.add(event_two);
        //MOCK ALERT: Return mocked result set on find
        when(eventService.findAll()).thenReturn(events);
        // Invokes method to test
        List<Event> response = eventService.findAll();
        //MOCK ALERT: verify the method was called
        verify(eventService).findAll();
    }

}
