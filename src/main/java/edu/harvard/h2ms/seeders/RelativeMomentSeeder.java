package edu.harvard.h2ms.seeders;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import edu.harvard.h2ms.domain.core.RelativeMoment;
import edu.harvard.h2ms.domain.core.User;
import edu.harvard.h2ms.repository.RelativeMomentRepository;

@Component
public class RelativeMomentSeeder {
	private RelativeMomentRepository relativeMomentRepository;
	
	@Autowired
	public RelativeMomentSeeder( RelativeMomentRepository relativeMomentRepository)
	{
		this.relativeMomentRepository = relativeMomentRepository;
	}
	
	@EventListener
	public void seed(ContextRefreshedEvent event) {
		seedRelativeMomentTable();
	}
	
	private void seedRelativeMomentTable() {
		if (relativeMomentRepository.count() == 0) {
			List<List<String>> records = asList(
					asList("RoomEntry", "Room entry."),
					asList("RoomExit", "Room exit.")
					);

			for (List<String> record : records) {
				String name = record.get(0);
				String description = record.get(1);

				RelativeMoment relativeMoment = new RelativeMoment();
				relativeMoment.setName(name);
				relativeMoment.setDescription(description);
				relativeMomentRepository.save(relativeMoment);

			}
		}
	}
	
}
