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
@RequestMapping(path="/events")
public class EventController {

    final Logger log = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private QuestionRepository questionRepository;
    

    /**
     * Rest Endpoint for retrieving all events in H2MS systems and returns results
     * grouped by a specified timeframe (ie. week, month, year, quarter)
     * Ex. /events/countBy/week
     * @param timeframe - week, month, year, quarter
     * @return
     */
    @RequestMapping(value = "/count/{timeframe}", method = RequestMethod.GET)
    public ResponseEntity<?> findEventCountByTimeframe(@PathVariable String timeframe) {
        log.info("Searching for all events grouping by " + timeframe);
        try {
        	return new ResponseEntity<Map<String, Long>>(eventService.findEventCountByTimeframe(timeframe), HttpStatus.OK);
        } catch(InvalidTimeframeException e) {
        	log.error(e.getMessage());
        	return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
    }

    /**
     * Rest End point for getting compliance of a specific question grouped by a
     * specified time frame (i.e. week, month, year, quarter) Ex.
     * /events/compliance/19/week.  Compliance is defined as percent of events
     * with a boolean question value to true.
     *
     * @param question - ID for Question
     * @param timeframe - week, month, year, quarter
     * @return
     */
    @RequestMapping(value = "/compliance/{questionId}/{timeframe}", method = RequestMethod.GET)
    public ResponseEntity<?> findComplianceByTimeframe(@PathVariable String timeframe, @PathVariable Long questionId) {
    	Question question = questionRepository.findOne(questionId);
    	log.info("Found question {}", question.toString());
    	
    	if(question.getAnswerType().equals("boolean")) {
    		try {
				return new ResponseEntity<Map<String, Double>>(eventService.findComplianceByTimeframe(timeframe, question), HttpStatus.OK);
			} catch (InvalidTimeframeException e) {
				log.error(e.getMessage());
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
    	} else {
    		return new ResponseEntity<String>(String.format("The question %l was not found.", questionId), HttpStatus.NOT_FOUND);
    	}
    }
}
