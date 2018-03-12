package edu.harvard.h2ms.web.controller;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.harvard.h2ms.domain.core.Event;
import edu.harvard.h2ms.domain.core.Method;
import edu.harvard.h2ms.repository.EventRepository;
import edu.harvard.h2ms.repository.MethodRepository;
import edu.harvard.h2ms.repository.UserRepository;

/**
 * 
 * @author user1
 *
 */
@RestController
@RequestMapping(path="/event")
public class EventController {
	
	private UserRepository userRepository;
	
	// Reference: https://stackoverflow.com/questions/29313687/trying-to-use-spring-boot-rest-to-read-json-string-from-post
	// Optional solution: http://www.baeldung.com/jackson-deserialization
	
	final Logger logger = LoggerFactory.getLogger(EventController.class);
	
	@Autowired
	EventRepository eventRepository; 
	
	@Autowired
	MethodRepository methodRepository;
	
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
		
		
		// attempt to get subject
//		Optional<Long> subject_id =  Optional.of(Long.valueOf((Integer)payload.get("subject_id")));
//		User subject = new User(); //userRepository.findOne(subject_id.get());
		event.setObservee(""+payload.get("subject_id"));
			
//		User observer = new User();
		event.setObserver(""+payload.get("observer_id"));
		
		event.setObservationType(""+payload.get("event_type_id"));
		
		Optional<Long> method_id =  Optional.of(Long.valueOf((Integer)payload.get("method_id")));
		Method method = methodRepository.findOne(method_id.get());
		
		event.setHandWashType(""+method);
		
		
		return eventRepository.save(event);
	}

}
