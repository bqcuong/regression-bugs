package edu.harvard.h2ms.service;

import com.google.common.collect.Lists;

import edu.harvard.h2ms.domain.core.Event;
import edu.harvard.h2ms.domain.core.Question;
import edu.harvard.h2ms.exception.InvalidTimeframeException;
import edu.harvard.h2ms.repository.AnswerRepository;
import edu.harvard.h2ms.repository.EventRepository;
import edu.harvard.h2ms.repository.QuestionRepository;
import edu.harvard.h2ms.service.utils.H2msRestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service("eventService")
@Repository
@Transactional
public class EventServiceImpl implements EventService {

	final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

	@Autowired
	private EventRepository eventRepository;

	/**
	 * Retrieves all events from the H2MS system, extracts the timestamps and parses
	 * them, returns count per distinctly parsed timestamp Ex. If system has 1 event
	 * with timestamp "2018-03-21T17:58:05.742+0000", results returned is {"March
	 * (2018)":1}
	 * 
	 * @param timeframe
	 *            - week, month, year, quarter
	 * @return
	 * @throws InvalidTimeframeException
	 */
	@Transactional(readOnly = true)
	public Map<String, Long> findEventCountByTimeframe(String timeframe) throws InvalidTimeframeException {

		List<Event> events = Lists.newArrayList(eventRepository.findAll());
		log.info("No. of events found: {}", events.size());

		Map<String, Set<Event>> groupedEvents = H2msRestUtils.groupEventsByTimestamp(events, timeframe);

		log.info("Parsed {} timestamps by {}", groupedEvents.size(), timeframe);

		return H2msRestUtils.frequencyCounter(groupedEvents);
	}

	/**
	 * Returns a compliance rate for a question over a time frame. Collects events
	 * by weekly, monthly, etc, and then calculates the compliance during that time
	 * frame.
	 * 
	 * @param String
	 *            time frame - week, month, year, quarter
	 * @param Question
	 *            question - question to calculate compliance for
	 * 
	 * @return Map of time frame name to compliance rate
	 * @throws InvalidTimeframeException
	 */
	@Transactional(readOnly = true)
	public Map<String, Double> findComplianceByTimeframe(String timeframe, Question question)
			throws InvalidTimeframeException {
		log.debug("Found event template {}", question.getEventTemplate().toString());

		// Get all events of that template
		List<Event> events = eventRepository.findByEventTemplate(question.getEventTemplate());
		log.debug("Found {} events", events.size());

		// Group events by time frame
		Map<String, Set<Event>> groupedEvents = H2msRestUtils.groupEventsByTimestamp(events, timeframe);

		for (Map.Entry<String, Set<Event>> entry : groupedEvents.entrySet()) {
			log.debug("Timeframe {} had {} events", entry.getKey(), entry.getValue().size());
		}

		// Calculate compliance for each grouping by time frame
		Map<String, Double> compliance = groupedEvents.entrySet().stream().collect(
				Collectors.toMap(e -> e.getKey(), e -> H2msRestUtils.calculateCompliance(question, e.getValue())));

		for (Map.Entry<String, Double> entry : compliance.entrySet()) {
			log.debug("Compliance for {} is {}", entry.getKey(), entry.getValue());
		}

		return compliance;
	}

	@Override
	public Map<String, Double> findComplianceByUserType(Question question) {
		// TODO Auto-generated method stub
		return null;
	}

}
