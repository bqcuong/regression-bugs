package edu.harvard.pallmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//TODO Remove this controller
@Controller
public class SPAController {

	@RequestMapping("/app")
	public String app() {
		return "spa";
	}
}
