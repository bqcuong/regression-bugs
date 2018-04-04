package edu.harvard.h2ms.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.harvard.h2ms.service.ReportService;

@RestController
@RequestMapping(path="/api/reports")
public class ReportController {
	
	@Autowired
	private ReportService reportService;
	
	
	@RequestMapping(value = "/events", method = RequestMethod.GET)
	public void getEventsReport(HttpServletResponse response){
		response.setContentType("text/plain; charset=utf-8");
		try {
			response.getWriter().print(reportService.createEventReport());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
