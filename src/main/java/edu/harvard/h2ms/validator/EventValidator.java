package edu.harvard.h2ms.validator;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.harvard.h2ms.domain.core.Event;
import edu.harvard.h2ms.domain.core.Question;

@Component("beforeCreateEventValidator")
public class EventValidator implements Validator {

	@Override
	public boolean supports(Class<?> klass) {
		return Event.class.equals(klass);
	}

	@Override
	public void validate(Object object, Errors errors) {
		Event event = (Event) object;
		
		System.out.println(event.toString());
		
		if(event.getSubject() == null) {
			errors.rejectValue("subject", "Event.RequiredSubject");
		}
		
		if(event.getObserver() == null) {
			errors.rejectValue("observer", "Event.RequiredObserver");
		}
		
		if(event.getTimestamp() == null) {
			errors.rejectValue("timestamp", "Event.RequiredTimestamp");
		}
		
		if(event.getEventTemplate() == null) {
			errors.rejectValue("eventTemplate", "Event.RequiredTemplate");
		} else {
			Set<Question> questions = event.getEventTemplate().getQuestions();
			Set<Question> required = event.getEventTemplate().getRequiredQuestions();
		
		
			/* Find any answers that aren't part of the event template */
			Boolean unknown = event
				.getAnswers()
				.stream()
				.filter(
						answer -> !questions.contains(answer.getQuestion())
						).
				findAny().
				isPresent();
			
			if(unknown) {
				errors.reject("Event.UnknownAnswer");
			}
			
			/* Find any missing required answers */
			event.getAnswers().stream().forEach(answer -> required.remove(answer.getQuestion()));
			
			if(!required.isEmpty()) {
				errors.reject("Event.RequiredAnswer");
			}
		}
		
	}
}
