package edu.harvard.h2ms.service;

import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

public interface UserService {

	/**
	 * Prepares a key-value mappings of the average hand washing compliance
	 * grouped by employee type. The average hand washing compliance is how
	 * many times an employee washed their hadns divided by the number
	 * of possible times an employee could have washed their hands.
	 * @return
	 */
	@Transactional(readOnly=true)
	public Map<String, Double> findAvgHandWashCompliance();

}
