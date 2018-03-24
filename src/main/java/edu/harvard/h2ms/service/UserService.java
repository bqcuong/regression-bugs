package edu.harvard.h2ms.service;

import edu.harvard.h2ms.domain.core.User;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

public interface UserService {

	void save(User user);

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
