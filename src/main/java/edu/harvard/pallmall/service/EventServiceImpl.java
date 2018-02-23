package edu.harvard.pallmall.service;

import com.google.common.collect.Lists;
import edu.harvard.pallmall.domain.Event;
import edu.harvard.pallmall.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * The EventService implements the EventService interface
 * and contains the mechanisms for finding and saving events.
 */
@Service("eventService")
@Repository
@Transactional
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;

    @Autowired
    public void setEventRepository(EventRepository EventRepository) {
        this.eventRepository = eventRepository;
    }

    // Finds all Events
    @Transactional(readOnly=true)
    public Iterable<Event> findAll() {
        return eventRepository.findAll();
    }

    // Finds an Event by its ID
    @Transactional(readOnly=true)
    public Event findById(Long id) {
        return eventRepository.findOne(id);
    }

    // Persists a new Event
    public Event save(Event event) {
        return eventRepository.save(event);
    }

}
