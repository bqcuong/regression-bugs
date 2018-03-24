package edu.harvard.h2ms.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import edu.harvard.h2ms.service.UserService;
import java.util.Map;

@RestController
@RequestMapping(path="/users")
public class UserController {

	final Logger log = LoggerFactory.getLogger(UserController.class);

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Rest Endpoint for retrieving the number of times an
	 * employee washed their hands out of the possible times
	 * an employee could have washed their hands.
	 * Ex. /avgWashed/
	 * @return
	 */
	@RequestMapping(value = "/avgWashed", method = RequestMethod.GET)
	public Map<String, Double> findAvgWashCompliance(){
		return userService.findAvgHandWashCompliance();
	}

}
