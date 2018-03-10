package edu.harvard.h2ms.service;

import edu.harvard.h2ms.domain.admin.Email;
import edu.harvard.h2ms.domain.core.Event;

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
