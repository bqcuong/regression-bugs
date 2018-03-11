package edu.harvard.h2ms.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import edu.harvard.h2ms.domain.core.Event;
import edu.harvard.h2ms.service.ManagementDashboardService;

import java.sql.Time;

/**
 * Event Rest Controller
 * URL's start with /demo (after Application path)
 */
@RestController
@RequestMapping(path="/managementDashboard")
public class ManagementDashboardController {

    final Logger logger = LoggerFactory.getLogger(ManagementDashboardController.class);

    private ManagementDashboardService managementDashboardService;

    @Autowired
    public void setManagementDashboardService(ManagementDashboardService managementDashboardService) {
        this.managementDashboardService = managementDashboardService;
    }

    /*
    TODO uncomment when db is setup
    @GetMapping(path="/all")
    public @ResponseBody Iterable<Event> getAllEvents() {
        logger.info("Finding all events archived.");
        // This returns a JSON or XML with the events
        return managementDashboardService.findAll();
    }
    */

    //todo this is a simple example of api endpoint to return event, this can be remove later
    //todo add in all the request params
    //todo update model w/ joins
    @RequestMapping("/eventexample")
    public Event example(@RequestParam(value="name", defaultValue="Test Form 1") String name) {
        Event event = new Event();
        event.setId(1L);
        event.setHandWashType("Soap");
        event.setObservationType("Sensor");
        event.setObservee("John Doe");
        event.setObserver("Jane Smith");
        event.setTimestamp(new Time(234234L));
        event.setRelativeMoment("Before Entering Room");
        return event;
    }

    //todo add rest call to send email

}