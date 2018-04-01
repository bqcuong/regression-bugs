package edu.harvard.h2ms.service;

import java.util.Map;

import edu.harvard.h2ms.domain.core.Question;
import edu.harvard.h2ms.exception.InvalidTimeframeException;

/**
 * Event Service ...
 */
public interface EventService {

	/**
	 * Retrieves all events in H2MS systems and returns results
	 * grouped by a specified time frame (i.e. week, month, year, quarter)
	 * @param timeframe - week, month, year, quarter
	 * @return Event count by time frame
	 * @throws InvalidTimeframeException 
	 */
	Map<String, Long> findEventCountByTimeframe(String timeframe) throws InvalidTimeframeException;

    /**
     * Retrieves compliance and returns results grouped by a specified
     * time frame (i.e. week, month, year, quarter)
     *
	 * @param timeframe - week, month, year, quarter
	 * @return Compliance by time frame
     * @throws InvalidTimeframeException 
	 */
	Map<String, Double> findComplianceByTimeframe(String timeframe, Question question) throws InvalidTimeframeException;
	
	/**
	 * Retrieves compliance and returns results grouped by a specified
	 * user type, e.g. Nurse, Doctor, etc.
	 * 
	 * @param question
	 * @return 
	 */
	Map<String, Double> findComplianceByUserType(Question question);
}
