package edu.harvard.h2ms.service;

import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import edu.harvard.h2ms.domain.core.*;
import edu.harvard.h2ms.repository.EventRepository;
import edu.harvard.h2ms.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;
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
	
	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	@Override
	public User findUserByResetToken(String resetToken) {
		return userRepository.findByResetToken(resetToken);
	}
	
	@Override
	public void save(User user) {
		userRepository.save(user);
	}
	
	

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private QuestionRepository questionRepository;

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
		String answerValue = "true";
		Double totalPopulation = null;
		Double washedPopulation = null;

		// Retrieves 'washed?' question object
		List<Question> questions = Lists.newArrayList(questionRepository.findAll());
		Optional<Question> keyQuestion = questions.stream()
				.filter(question -> question.getQuestion().equals("Washed?"))
				.findAny();

		for (String type : distinctUserTypes) {
			if(type == null) { continue; }

			// Retrieves total population (ie. how many times the questions was asked for each user type)
			totalPopulation = Double.valueOf(events.stream()
					.filter(event -> event.getSubject().getType().equals(type)).count());

			// Retrieves the total count of events per user type where user took opportunity to wash hands
			washedPopulation = Double.valueOf(events.stream()
					.filter(event -> event.getSubject().getType().equals(type) &&
							getAnswer(event.getAnswers(), keyQuestion).equals(answerValue))
					.count());

			// Calculates the average hand wash compliance for a given type
			if(totalPopulation != null && washedPopulation != null)
				dataMap.put(type, calculateAverage(washedPopulation, totalPopulation));

		}

		return dataMap;
	}

	/**
	 * Retrieves the provided answer for a particular question
	 * @param answers
	 * @param question
	 * @return
	 */
	public String getAnswer(Set<Answer> answers, Optional<Question> question) {
		for(Answer answer : answers) {
			if(answer.getQuestion().getQuestion().equals(question.get().getQuestion())) {
				return answer.getValue();
			}
		}
		return null;
	}

}
