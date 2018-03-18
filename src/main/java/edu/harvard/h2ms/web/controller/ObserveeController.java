package edu.harvard.h2ms.web.controller;

import edu.harvard.h2ms.domain.core.Observee;
import edu.harvard.h2ms.service.ObserveeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Observee Controller...
 */
@RestController
@RequestMapping(value = "/observees")
public class ObserveeController {

    final Logger logger = LoggerFactory.getLogger(ObserveeController.class);

    private ObserveeService observeeService;

    @Autowired
    public void setObserveeService(ObserveeService observeeService) {
        this.observeeService = observeeService;
    }

    // List all observees
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Observee> list() {
        List<Observee> observees = observeeService.findAll();
        logger.info("No. of observees: " + observees.size());
        return observees;
    }

    // Finds a single observee by specified ID
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Observee show(@PathVariable("id") Long id) {
        Observee observee = observeeService.findById(id);
        logger.info("Found observee with ID: " + id);
        return observee;
    }

    // FIXME - Update with input parameters that UI will pass
    @RequestMapping("/create")
    public Observee create() {
        // Example of persisting new observee
        Observee observee = new Observee();
        observee.setId(1L);
        observee.setFirstName("Michael");
        observee.setLastName("Delaney");
        observeeService.save(observee);
        return observee;
    }

}
