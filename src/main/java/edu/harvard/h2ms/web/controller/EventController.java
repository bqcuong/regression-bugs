package edu.harvard.h2ms.web.controller;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.harvard.h2ms.domain.core.Event;
import edu.harvard.h2ms.domain.core.Method;
import edu.harvard.h2ms.domain.core.User;
import edu.harvard.h2ms.exception.ResourceNotFoundException;
import edu.harvard.h2ms.repository.EventRepository;
import edu.harvard.h2ms.repository.MethodRepository;
import edu.harvard.h2ms.repository.UserRepository;
import edu.harvard.h2ms.service.EventService;

/**
 * 
 * @author user1
 *
 */
@RestController
@RequestMapping(path="/events")
public class EventController {
	
	
	// Reference: https://stackoverflow.com/questions/29313687/trying-to-use-spring-boot-rest-to-read-json-string-from-post
	// Optional solution: http://www.baeldung.com/jackson-deserialization
	
	final Logger logger = LoggerFactory.getLogger(EventController.class);
	
	@Autowired
	EventRepository eventRepository; 
	
	@Autowired
	MethodRepository methodRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private EventService eventService;
	
	// Retrieve all events
	@GetMapping()
	public List<Event> getAllEvents(){
		return eventService.getAllEvents();
	}
	
	// Retrieve an event
	@GetMapping(value = "/{id}")
	public Event getEvent(@PathVariable Long id) {
		return eventService.getEvent(id);
	}
	
	// Create a new Event
	@PostMapping()
	public Event createEvent(@Valid @RequestBody Map<String, Object> payload) {
		
		Event event = new Event();
		
		// parse time
		// https://stackoverflow.com/questions/22463062/how-to-parse-format-dates-with-localdatetime-java-8
		CharSequence dateTime = (CharSequence)payload.get("date-time");
		DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		LocalDateTime dateTime2 = LocalDateTime.from(f.parse(dateTime));
		// All this work for just this.
		event.setTimestamp(new Time(dateTime2.toEpochSecond(ZoneOffset.ofHours(0))));
		
		// relativeMoment
		event.setRelativeMoment(""+payload.get("relativeMoment"));
		
		
		// attempt to get subject
		Optional<Long> subject_id =  Optional.of(Long.valueOf((Integer)payload.get("subject_id")));
		User subject = userRepository.findOne(subject_id.get());
		if(subject == null) 
			throw new ResourceNotFoundException(subject_id.get(), "user not found");
			
		event.setObservee(""+payload.get("subject_id"));
			
		
		// get observer
		Optional<Long> observer_id = Optional.of(Long.valueOf((Integer)payload.get("observer_id")));
		User observer = userRepository.findOne(observer_id.get());
		if(observer == null)
			throw new ResourceNotFoundException(observer_id.get(), "user not found");
		event.setObserver(""+payload.get("observer_id"));
		
		
		event.setObservationType(""+payload.get("event_type_id"));
		
		Optional<Long> method_id =  Optional.of(Long.valueOf((Integer)payload.get("method_id")));
		Method method = methodRepository.findOne(method_id.get());
		if(method == null)
			throw new ResourceNotFoundException(method_id.get(), "method not found");
		
		event.setMethod(method.getId());
		
		
		return eventService.addEvent(event);
	}

}
