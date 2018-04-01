package edu.harvard.h2ms.web.controller;

import edu.harvard.h2ms.domain.core.Question;
import edu.harvard.h2ms.exception.InvalidTimeframeException;
import edu.harvard.h2ms.repository.QuestionRepository;
import edu.harvard.h2ms.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping(path = "/events")
public class EventController {

	final Logger log = LoggerFactory.getLogger(EventController.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private QuestionRepository questionRepository;

	/**
	 * Rest end point for retrieving all events in H2MS systems and returns results
	 * grouped by a specified time frame (i.e. week, month, year, quarter)
	 * 
	 * Example: /events/count/week
	 * 
	 * @param timeframe
	 *            - week, month, year, quarter
	 * @return ResponseEntity
	 * 				- 200 OK with JSON Map<String, Long> with results
	 * 				- 400 Bad Request on incorrect time frame
	 */
	@RequestMapping(value = "/count/{timeframe}", method = RequestMethod.GET)
	public ResponseEntity<?> findEventCountByTimeframe(@PathVariable String timeframe) {
		log.info("Searching for all events grouping by " + timeframe);
		
		try {
			return new ResponseEntity<Map<String, Long>>(eventService.findEventCountByTimeframe(timeframe),
					HttpStatus.OK);
		} catch (InvalidTimeframeException e) {
			log.error(e.getMessage());
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * Rest end point for getting compliance of a specific question grouped by a
	 * specified time frame (i.e. week, month, year, quarter) Compliance is defined
	 * as percent of events with a boolean question value to true.
	 * 
	 * Example: /events/compliance/19/week
	 *
	 * @param questionId
	 *            - ID for Question
	 * @param timeframe
	 *            - week, month, year, quarter
	 * 
	 * @return ResponseEntity - 200 OK with JSON Map<String, Double> with compliance
	 *         results - 400 Bad Request on incorrect time frame - 404 Not Found if
	 *         question not found
	 * 
	 */
	@RequestMapping(value = "/compliance/{questionId}/{timeframe}", method = RequestMethod.GET)
	public ResponseEntity<?> findComplianceByTimeframe(@PathVariable String timeframe, @PathVariable Long questionId) {		
		Question question = questionRepository.findOne(questionId);
		if(question == null) {
			return new ResponseEntity<String>(String.format("The question %l was not found.", questionId), HttpStatus.NOT_FOUND);
		}
			
		log.debug("Found question {}", question.toString());
		
		if (question.getAnswerType().equals("boolean")) {
			try {
				return new ResponseEntity<Map<String, Double>>(
						eventService.findComplianceByTimeframe(timeframe, question), HttpStatus.OK);
			} catch (InvalidTimeframeException e) {
				log.error(e.getMessage());
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
		} else {
			// Invalid question type:
			String message = String.format("Compliance data can only be generated for a boolean question. Question is '%s.'", question.getAnswerType());
			log.error(message);				
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}
}
