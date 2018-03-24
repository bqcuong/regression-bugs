package edu.harvard.h2ms.seeders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.harvard.h2ms.domain.core.*;
import edu.harvard.h2ms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.Transient;
import java.util.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;

@Component
public class EventSeeder {

    @JsonIgnore
    @Transient
    @Autowired
    private PasswordEncoder passwordEncoder;

    private EventRepository eventRepository;
    private AnswerRepository answerRepository;
    private UserRepository userRepository;
    private EventTemplateRepository eventTemplateRepository;
    private QuestionRepository questionRepository;

    @Autowired
    public EventSeeder(EventRepository eventRepository, AnswerRepository answerRepository, UserRepository userRepository,
                       EventTemplateRepository eventTemplateRepository, QuestionRepository questionRepository) {
        this.eventRepository = eventRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.eventTemplateRepository = eventTemplateRepository;
        this.questionRepository = questionRepository;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedEventTable();
    }

    private void seedEventTable() {

        List<List<String>> records = asList(
                asList("jane", "smith", "jane.smith@email.com", "Dietician", "Y"),
                asList("john", "doe", "john.doe@email.com", "Surgical Tech", "N"),
                asList("luke", "skywalker", "luke.walker@email.com", "Other", "Y"),
                asList("jon", "snow", "jon.snow@email.com", "Other", "Y"),
                asList("arya", "stark", "arya.stark@email.com", "Surgical Tech", "Y"),
                asList("joffrey", "baratheon", "joffrey.baratheon@email.com", "Other", "N")
        );

        User observer = new User();
        observer.setEmail("someting@something.com");
        observer.setPassword("asdsdffasdf");
        observer.setFirstName("lil jon");
        observer.setLastName("whasdfsaf");
        observer.setType("Other");
        observer.setNotificationFrequency("2");
        observer.setMiddleName("asdf");
        userRepository.save(observer);

        for (List<String> record : records) {
            String firstName = record.get(0);
            String lastName = record.get(1);
            String email = record.get(2);
            String userType = record.get(3);
            String washedStatus = record.get(4);

            // Event Template must be persisted prior to Event persistence
            EventTemplate eventTemplate = new EventTemplate();
            eventTemplate.setName("Event_Template_1");
            eventTemplateRepository.save(eventTemplate);

            // Question must be persisted prior to Answer persistence
            Question question = new Question();
            question.setAnswerType("testvalue_answer");
            question.setRequired(TRUE);
            question.setOptions(Arrays.asList("Option1", "Option2"));
            question.setQuestion("What is ... ?");
            question.setPriority(1);
            question.setEventTemplate(eventTemplate);
            questionRepository.save(question);
            Set<Question> questions = new HashSet<>();

            // Answer must be persisted prior to Event persistence
            Answer answer = new Answer();
            answer.setAnswerType("testvalue_answer");
            answer.setValue("testvalue_value");
            answerRepository.save(answer);

            // User must be persisted prior to Event persistence
            User user = new User();
            user.setEmail(email);
            user.setPassword("asdfasdf");
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setType(userType);
            user.setNotificationFrequency("1");
            userRepository.save(user);

            // Creates and persists event
            Event event = new Event();
            Set<Answer> answers = new HashSet<>();
            event.setAnswers(answers);
            event.setLocation("testvalue_location");
            event.setSubject(user);
            event.setEventTemplate(eventTemplate);
            event.setObserver(observer);
            event.setTimestamp(new Date(System.currentTimeMillis()));
            if (washedStatus.equals("Y")) {
                event.setWashed(TRUE);
            } else {
                event.setWashed(FALSE);
            }
            eventRepository.save(event);
        }

    }
}