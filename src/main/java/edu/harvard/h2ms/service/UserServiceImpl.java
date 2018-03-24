package edu.harvard.h2ms.service;

import com.google.common.collect.Lists;
import edu.harvard.h2ms.domain.core.Event;
import edu.harvard.h2ms.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import edu.harvard.h2ms.domain.core.User;
import edu.harvard.h2ms.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import static edu.harvard.h2ms.service.utils.H2msRestUtils.calculateAverage;
import static java.lang.Boolean.TRUE;

@Service("userService")
@Repository
@Transactional
public class UserServiceImpl implements UserService {

	final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EventRepository eventRepository;

	@Override
	@Transactional(readOnly=true)
	public Map<String, Double> findAvgHandWashCompliance() {

		// Fetches all users from H2MS database
		List<User> users = Lists.newArrayList(userRepository.findAll());
		log.info("No. of users found: {}", users.size());
		if(users.isEmpty()) { return null; }

		// Determines all the distinct types of users
		List<String> distinctUserTypes = users.stream().map(User::getType).collect(Collectors.toList());
		log.info("There are {} distinct user types ", distinctUserTypes.size());
		if(distinctUserTypes.isEmpty()) { return null; }

		// Fetches all events from the H2MS database
		List<Event> events = Lists.newArrayList(eventRepository.findAll());
		log.info("No. of events found: {}", events.size());
		if(events.isEmpty()) { return null; }

		return calcAvgHandWashCompByDistinctUserType(distinctUserTypes, events);
	}

	public Map<String, Double> calcAvgHandWashCompByDistinctUserType(List<String> distinctUserTypes, List<Event> events){

		Map<String, Double> dataMap = new HashMap<String, Double>();
		Double totalPopulation;
		Double washedPopulation;

		for (String type : distinctUserTypes) {
			if(type == null) { continue; }

			// Retrieves the total count of events per user type
			Stream<Event> eventStream = events.stream().filter(event -> event.getSubject().getType().equals(type));
			if (eventStream != null) {
				totalPopulation = Double.valueOf(eventStream.count());
			} else{
				log.warn("There are no events from user type {}", type);
				continue;
			}

			// Retrieves the total count of events per user type where user took opportunity to wash hands
			eventStream = events.stream().filter(event -> event.getSubject().getType().equals(type) && event.getWashed()==TRUE);
			if(eventStream != null) {
				washedPopulation = Double.valueOf(eventStream.count());
			} else {
				log.warn("No users of any user type {} took the opportunity to wash their hands.", type);
				continue;
			}

			// Calculates the average hand wash compliance for a given type
			if(totalPopulation != null && totalPopulation != null) {
				dataMap.put(type, calculateAverage(washedPopulation, totalPopulation));
			} else {
				log.warn("No users of any user type {} took the opportunity to wash their hands.", type);
				continue;
			}

		}

		return dataMap;
	}

}