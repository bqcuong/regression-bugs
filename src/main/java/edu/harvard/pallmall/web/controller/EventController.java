package edu.harvard.pallmall.web.controller;

import edu.harvard.pallmall.domain.Event;
import edu.harvard.pallmall.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Event Rest Controller
 * URL's start with /demo (after Application path)
 */
@Controller
@RequestMapping(path="/event")
public class EventController {

    final Logger logger = LoggerFactory.getLogger(EventController.class);

    private EventService eventService;

    @Autowired
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<Event> getAllEvents() {
        logger.info("Finding all events archived.");
        // This returns a JSON or XML with the events
        return eventService.findAll();
    }
}