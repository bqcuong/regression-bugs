package edu.harvard.h2ms.seeders;

import static java.lang.Boolean.TRUE;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import static java.util.Arrays.asList;

@Component
public class EventSeeder {

	final Logger log = LoggerFactory.getLogger(EventSeeder.class);
	@Autowired
	private EventRepository eventRepository;

	@Autowired
	UserRepository userRepository;

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
			User observer = new User("John2", "Quincy2", "Adams2", EMAIL, PASSWORD, "other");
			userRepository.save(observer);
			User subject = new User("Jane2", "Doe2", "Sa2m", "sample2@email.com", "password", "doctor");
			userRepository.save(subject);

			List<List<String>> records = asList(
					asList("true"), asList("false"), asList("true"), asList("true"));

			for (List<String> record : records) {

				String qval0 = record.get(0);

				// Creates and persists event
				Event event = new Event();
				Set<Answer> answers = new HashSet<>();
				Answer answer = new Answer();
				Question question = new Question();
				question.setPriority(1);
				question.setRequired(TRUE);
				question.setAnswerType("Boolean");
				question.setQuestion("Washed?");
				answer.setQuestion(question);
				// values are: true, false
				answer.setValue(qval0);
				answers.add(answer);
				event.setAnswers(answers);
				event.setLocation("Location_01");
				event.setSubject(subject);
				event.setObserver(observer);
				event.setEventTemplate(eventTemplateRepository.findByName("Handwashing Event"));
				event.setObserver(observer);
				event.setTimestamp(new Date(System.currentTimeMillis()));
				eventRepository.save(event);

			}
		}
	}
}