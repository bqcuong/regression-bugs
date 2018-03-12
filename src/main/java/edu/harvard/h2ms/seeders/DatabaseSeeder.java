package edu.harvard.h2ms.seeders;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import edu.harvard.h2ms.domain.core.Method;
import edu.harvard.h2ms.domain.core.User;
import edu.harvard.h2ms.repository.MethodRepository;
import edu.harvard.h2ms.repository.UserRepository;


/**
 * Seeds initial data into the database, example taken from
 * https://dzone.com/articles/how-to-create-a-database-seeder-in-spring-boot
 */
@Component
public class DatabaseSeeder {
    private MethodRepository methodRepository;
    private UserRepository userRepository;

    @Autowired
    public DatabaseSeeder(
            MethodRepository methodRepository,
            UserRepository userRepository
            ) 
    {
        this.methodRepository = methodRepository;
        this.userRepository   = userRepository;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedMethodsTable();
//        seedUserTable();
    }

    private void seedMethodsTable() {
        List<List<String>> records = asList(
                asList("Soap and water", "Handwashing method that involves soap and water in a sink."),
                asList("Alcohol", "Hand sanitizing station")
                );

        for(List<String> record : records) {
            String name = record.get(0);
            String description = record.get(1);

            if(methodRepository.findByName(name).isEmpty()) {
                Method method = new Method();
                method.setName(name);
                method.setDescription(description);
                methodRepository.save(method);
            }
        }
    }
    
    private void seedUserTable() {
    	List<List<String>> records = asList(
    			asList("jane", "doe", "jane.doe@email.com"),
    			asList("john", "doe", "john.doe@email.com")
    			);
    	
    	for(List<String> record : records) {
    		String firstName = record.get(0);
    		String lastName  = record.get(1);
    		String email     = record.get(2);
    		
    		if(userRepository.findByEmail(email).isEmpty()) {
    			User user = new User();
    			user.setFirstName(firstName);
    			user.setLastName(lastName);
    			user.setEmail(email);
    			
    		}
    	}
    			
    }
}
