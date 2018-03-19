package edu.harvard.h2ms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.harvard.h2ms.domain.core.Event;
import edu.harvard.h2ms.repository.EventRepository;

/**
 * Contains the business logic and call methods in the repository layer
 * https://www.codebyamir.com/blog/create-rest-api-with-spring-boot
 */
@Service
public class EventService {
	
	@Autowired
	private EventRepository eventRepository;
	
	//
	public List<Event> getAllEvents() {
		List<Event> events = new ArrayList<Event>();
		eventRepository.findAll().forEach(events::add);
		return events;
	}
	
	public Event getEvent(Long id) {
		return eventRepository.findOne(id);
	}
	
	public Event addEvent(Event event) {
		eventRepository.save(event);
		return event;
	}
	
	public void deleteEvent(Long id) {
		eventRepository.delete(id);
	}
	
	

}
