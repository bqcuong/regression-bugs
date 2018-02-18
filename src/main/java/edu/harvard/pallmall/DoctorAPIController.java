package edu.harvard.pallmall;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctors")
public class DoctorAPIController {
	
	
	/**
	 * try: http://localhost:8080/doctors
	 * @param name
	 * @return
	 */
	@GetMapping
	public Doctor getDoctor(@RequestParam(required = false, defaultValue = "") String name) {
		return new Doctor("John", "Holliday");
	}
	
	/**
	 * http://localhost:8080/doctors/123
	 * @return
	 */
	@GetMapping("{id}")
	public Doctor show(@PathVariable long id) {
		return new Doctor("John", "Holliday");
	}
	
	/**
	 * post session like:
	 * curl -d '{"firstName":"John","lastName":"Holliday"}' \
	 *      -H "Content-Type: application/json" -X POST localhost:8080/doctors; echo
	 * @param request
	 * @return
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Doctor postDoctor(@RequestBody Doctor request
    		) {
		
		return request;
	}
	

}
