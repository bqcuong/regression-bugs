package edu.harvard.pallmall.web.controller;

import edu.harvard.pallmall.domain.Event;
import edu.harvard.pallmall.service.ManagementDashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @RequestMapping("/eventexample")
    public Event example(@RequestParam(value="name", defaultValue="Test Form 1") String name) {
        Event event = new Event();
        event.setId(1L);
        event.setFormName(name);
        return event;
    }
}