package edu.harvard.h2ms.seeders;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import edu.harvard.h2ms.domain.core.Answer;
import edu.harvard.h2ms.domain.core.Event;
import edu.harvard.h2ms.domain.core.Question;
import edu.harvard.h2ms.domain.core.User;
import edu.harvard.h2ms.repository.EventRepository;
import edu.harvard.h2ms.repository.EventTemplateRepository;
import edu.harvard.h2ms.repository.QuestionRepository;
import edu.harvard.h2ms.repository.UserRepository;

@Component
public class EventSeeder {

	final Logger log = LoggerFactory.getLogger(EventSeeder.class);
	@Autowired
    private EventRepository eventRepository;
	
	@Autowired UserRepository userRepository;
    
	@Autowired
    private QuestionRepository questionRepository;
	
	@Autowired
	private EventTemplateRepository eventTemplateRepository;

    @Autowired
    public EventSeeder(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedEventTable();
    }

    private void seedEventTable() {
       if (eventRepository.count() == 0) {
    	   
    	   
    	   Event event = new Event();
    	   Answer answer = new Answer();    	   
//    	   Question question = questionRepository.findByQuestion("Washed?");    	
//    	   Iterable<Question> questions = questionRepository.findAll();
    	   
    	   
    	   Question question = new Question();
    	   question.setQuestion("Washed?");
    	   question.setAnswerType("boolean");
    	   question.setRequired(false);
    	   question.setPriority(0);
    	   
    	   answer.setQuestion(question);
    	   answer.setValue("true");
    	   Set<Answer> answers = new HashSet<>();
    	   answers.add(answer);
    	   event.setAnswers(answers);
    	   User user = userRepository.findByFirstName("another");
    	   event.setSubject(user);
    	   event.setObserver(user);
    	   event.setLocation("location");
    	   event.setEventTemplate(eventTemplateRepository.findByName("Handwashing Event"));
    	   event.setTimestamp(new Date());
    	   
    	   eventRepository.save(event);
    	   
    	   
       }
    }
}