package edu.harvard.h2ms.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.harvard.h2ms.domain.core.Question;
import edu.harvard.h2ms.exception.InvalidTimeframeException;
import edu.harvard.h2ms.repository.QuestionRepository;
import edu.harvard.h2ms.service.EventService;
import edu.harvard.h2ms.service.UserService;
import java.util.Map;

@RestController
@RequestMapping(path="/users")
public class UserController {

	final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private EventService eventService;
	
	@Autowired
	private QuestionRepository questionRepository;
		
	/**
	 * Rest end point for retrieving the number of times an
	 * employee type washed their hands out of the possible times
	 * an employee could have washed their hands.
	 * 
	 * Example: /users/compliance/3
	 * 
	 * @return
	 */
	@RequestMapping(value = "/compliance/{questionId}", method = RequestMethod.GET)
	public ResponseEntity<?> findCompliance(@PathVariable Long questionId){
		Question question = questionRepository.findOne(questionId);
		if(question == null) {
			return new ResponseEntity<String>(String.format("The question %l was not found.", questionId), HttpStatus.NOT_FOUND);
		}
			
		log.debug("Found question {}", question.toString());
		
		if (question.getAnswerType().equals("boolean")) {
			return new ResponseEntity<Map<String, Double>>(eventService.findComplianceByUserType(question), HttpStatus.OK);
		} else {
			// Invalid question type:
			String message = String.format("Compliance data can only be generated for a boolean question. Question is '%s.'", question.getAnswerType());
			log.error(message);				
			return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
		}
	}
}
