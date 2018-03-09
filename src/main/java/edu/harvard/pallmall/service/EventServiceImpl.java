package edu.harvard.pallmall.service;

import edu.harvard.pallmall.domain.core.Event;
import edu.harvard.pallmall.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("eventService")
@Repository
@Transactional
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;

    @Autowired
    public void setEventRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // Saves a new event
    public Event save(Event event) {
        return eventRepository.save(event);
    }


}
