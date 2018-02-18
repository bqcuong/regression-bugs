package edu.harvard.pallmall;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SPAController {

	@RequestMapping("/app")
	public String app() {
		return "spa";
	}
}
