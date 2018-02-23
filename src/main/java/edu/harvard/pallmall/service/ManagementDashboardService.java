package edu.harvard.pallmall.service;

import edu.harvard.pallmall.domain.admin.Email;
import edu.harvard.pallmall.domain.core.Event;

/**
 * The ManagementDashboardService...
 */
public interface ManagementDashboardService {

    // Finds all Events
    Iterable<Event> findAllEvents();

    // Finds an Event by its ID
    Event findEventById(Long id);

    // Persists a new Event
    Event saveEvent(Event event);

    void sendEmail(Email email);

}
