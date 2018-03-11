package edu.harvard.h2ms.service;

import edu.harvard.h2ms.domain.core.Event;
import java.util.List;

/**
 * EventService
 */
public interface EventService {

    // Save an event
    Event save(Event event);

    // Finds all events
    List<Event> findAll();

    // Find an event by its ID
    Event findById(Long id);


}
