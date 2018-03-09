package edu.harvard.pallmall.web.controller;

import edu.harvard.pallmall.domain.core.Event;
import edu.harvard.pallmall.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;

@RestController
@RequestMapping(value = "/events")
public class EventController {

    final Logger logger = LoggerFactory.getLogger(EventController.class);

    private EventService eventService;

    @Autowired
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

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

}
