package edu.harvard.h2ms.seeders;

import static java.lang.Boolean.TRUE;

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
    
    static final String EMAIL = "jqadams2@h2ms.org";
    static final String PASSWORD = "password";

    private void seedEventTable() {
       if (eventRepository.count() == 0) {
    	   

           // Sample User Data
           User observer = new User("John2", "Quincy2", "Adams2", EMAIL, PASSWORD);
           observer.setType("Other");
           userRepository.save(observer);
           User subject = new User("Jane2", "Doe2", "Sa2m", "sample2@email.com", "password");
           subject.setType("Doctor");
           userRepository.save(subject);

           // Creates and persists event
//           Event event = new Event();
//           Set<Answer> answers = new HashSet<>();
//           Answer answer = new Answer();
//           Question question = new Question();
//           question.setPriority(1);
//           question.setRequired(TRUE);
//           question.setAnswerType("Boolean");
//           question.setQuestion("Washed?");
//           answer.setQuestion(question);
//           answer.setValue("true");
//           answers.add(answer);
//           event.setAnswers(answers);
//           event.setLocation("Location_01");
//           event.setSubject(subject);
//           event.setObserver(observer);
//           event.setEventTemplate(eventTemplateRepository.findByName("Handwashing Event"));
//           event.setObserver(observer);
//           event.setTimestamp(new Date(System.currentTimeMillis()));
//           eventRepository.save(event);
           
           
//    	   Event event = new Event();
//    	   Answer answer = new Answer();    	   
//    	   Question question = questionRepository.findByQuestion("Washed?");    	
//    	   Iterable<Question> questions = questionRepository.findAll();
    	   
    	   
//    	   Question question = new Question();
//    	   question.setQuestion("Washed?");
//    	   question.setAnswerType("boolean");
//    	   question.setRequired(false);
//    	   question.setPriority(0);
//    	   
//    	   answer.setQuestion(question);
//    	   answer.setValue("true");
//    	   Set<Answer> answers = new HashSet<>();
//    	   answers.add(answer);
//    	   event.setAnswers(answers);
//    	   User user = userRepository.findByFirstName("another");
//    	   event.setSubject(user);
//    	   event.setObserver(user);
//    	   event.setLocation("location");
//    	   event.setEventTemplate(eventTemplateRepository.findByName("Handwashing Event"));
//    	   event.setTimestamp(new Date());
//    	   
//    	   eventRepository.save(event);
    	   
    	   
       }
    }
}