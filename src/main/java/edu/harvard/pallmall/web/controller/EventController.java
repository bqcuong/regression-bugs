package edu.harvard.pallmall.web.controller;

import edu.harvard.pallmall.domain.core.Event;
import edu.harvard.pallmall.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.util.List;

@RestController
@RequestMapping(value = "/events")
public class EventController {

    final Logger logger = LoggerFactory.getLogger(EventController.class);

    private EventService eventService;

    @Autowired
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    /*
    // FIXME - Update with input parameters that UI will pass
    @RequestMapping("/create")
    public Event create() {
        // Example of persisting new event
        Event event = new Event();
        event.setId(1L);
        event.setHandWashType("Soap");
        event.setObservationType("Hand Wash");
        event.setObservee("John Smith");
        event.setObserver("Jane Doe");
        event.setRelativeMoment("Before Entering Room");
        eventService.save(event);
        return event;
    }
    */

    // List all events
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Event> list() {
        List<Event> events = eventService.findAll();
        logger.info("No. of events: " + events.size());
        return events;
    }

    // Finds a single event by specified ID
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Event show(@PathVariable("id") Long id) {
        Event event = eventService.findById(id);
        return event;
    }


}
