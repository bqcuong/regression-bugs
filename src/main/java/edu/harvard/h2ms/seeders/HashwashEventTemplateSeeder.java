package edu.harvard.h2ms.seeders;

import static java.util.Arrays.asList;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import edu.harvard.h2ms.domain.core.EventTemplate;
import edu.harvard.h2ms.domain.core.Question;
import edu.harvard.h2ms.repository.EventTemplateRepository;
import edu.harvard.h2ms.repository.QuestionRepository;

@Component
public class HashwashEventTemplateSeeder {
    private EventTemplateRepository eventTemplateRepository;
    private QuestionRepository questionRepository;
    
    @Autowired
    public HashwashEventTemplateSeeder(EventTemplateRepository eventTemplateRepository, QuestionRepository questionRepository) 
    {
        this.eventTemplateRepository = eventTemplateRepository;
        this.questionRepository = questionRepository;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {
    	EventTemplate template = new EventTemplate("Handwashing Event");
    	eventTemplateRepository.save(template);
    	
    	Set<Question> questions = Stream.of(
    			new Question(
    					"Opportunity to wash?",
    					"boolean",
    					null,
    					true,
    					1,
    					template
    					),
    			
    			new Question(
    					"Washed?",
    					"boolean",
    					null,
    					true,
    					2,
    					template
    					),
    			
    			new Question(
    					"Relative moment",
    					"options",
    					asList("Room Entry", "Room Exit"),
    					true,
    					3,
    					template
    					)
    			).collect(Collectors.toSet());
    	
    	questionRepository.save(questions);
    }    
}
