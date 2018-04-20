package edu.harvard.h2ms.web.controller;

import edu.harvard.h2ms.domain.core.Location;
import edu.harvard.h2ms.service.LocationService;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/locations")
public class LocationController {

  final Logger log = LoggerFactory.getLogger(LocationController.class);

  @Autowired private LocationService locationService;

  /**
   * Rest end point for retrieving the compliance to a particular question. Compliance is calculated
   * as the percent of "true" values compared to the total population of answers.
   *
   * <p>Ex. /users/compliance/3
   *
   * @return
   */
  @RequestMapping(value = "/topLevel", method = RequestMethod.GET)
  public Set<Location> findTopLevel() {
    return locationService.findTopLevel();
  }
}
