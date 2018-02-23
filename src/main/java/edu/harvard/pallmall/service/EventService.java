package edu.harvard.pallmall.service;

import edu.harvard.pallmall.domain.Event;
import java.util.List;

/**
 * The EventService Interface described the functions that can be used on Event data.
 */
public interface EventService {

    // Finds all Events
    Iterable<Event> findAll();

    // Finds an Event by its ID
    Event findById(Long id);

    // Persists a new Event
    Event save(Event event);

}
