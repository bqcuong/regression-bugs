package edu.harvard.h2ms.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import edu.harvard.h2ms.domain.core.User;
import edu.harvard.h2ms.service.SecurityService;
import edu.harvard.h2ms.service.UserService;
import edu.harvard.h2ms.validator.UserValidator;
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

	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private UserValidator userValidator;
	
	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String registration(Model model) {
		model.addAttribute("userForm", new User());
		
		return "registration";
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
