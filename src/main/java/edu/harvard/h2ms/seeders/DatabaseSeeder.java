package edu.harvard.h2ms.seeders;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import edu.harvard.h2ms.domain.core.Method;
import edu.harvard.h2ms.repository.MethodRepository;


/**
 * Seeds initial data into the database, example taken from
 * https://dzone.com/articles/how-to-create-a-database-seeder-in-spring-boot
 */
@Component
public class DatabaseSeeder {
    private MethodRepository methodRepository;
    

    @Autowired
    public DatabaseSeeder(
            MethodRepository methodRepository
            ) 
    {
        this.methodRepository = methodRepository;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedMethodsTable();
    }

    private void seedMethodsTable() {
    	
    	System.out.println("*****************seedMethods");
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
    
}
