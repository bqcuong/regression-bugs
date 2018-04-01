package edu.harvard.h2ms.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

@Component
@Service("reportService")
public class ReportServiceImpl implements ReportService{
	
	final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);
	
	@Autowired
	private UserService userService;
	
	

	@Override
	public String createReport() {
		
//		Map<String, Double> complianceData = userService.findAvgHandWashCompliance();
		
		
		Map<String, Double> complianceData = new HashMap<String, Double>();
		complianceData.put("abc", 1.3);
		complianceData.put("dec", 1.3);
		complianceData.put("ghc", 1.3);
		
		
		Writer writer = new StringWriter();
//		StatefulBeanToCsv<MyReport> beanToCsv = new StatefulBeanToCsvBuilder(writer)
//				.withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
//				.build();
		
        CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
		
		List<MyReport> myReports = new ArrayList<>();
		if(complianceData == null) {
			log.error("****************no report retrieved");
		}
		
		complianceData.forEach((k,v) -> csvWriter.writeNext(new String[] {(String)k,((Double)v).toString()}));
		csvWriter.writeNext(new String[] {"hi","1.0"});
//		complianceData.forEach((k,v) -> myReports.add(new MyReport("hi",1.0)));
//		myReports.add(new MyReport("hi",1.0));
//		myReports.add(new MyReport("hi",1.0));
//		try {
////			beanToCsv.write(myReports);
//		} catch (CsvDataTypeMismatchException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (CsvRequiredFieldEmptyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		try {
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.info(writer.toString());
		
		return writer.toString();
	}
	
	
	private class MyReport {
		private String userType;
		private Double complianceRate;
		
		@SuppressWarnings("unused")
		protected MyReport() {}
		
		@SuppressWarnings("unused")
		protected MyReport(String userType, Double complianceRate) {
			this.userType = userType;
			this.complianceRate = complianceRate;
		}

		protected String getUserType() {
			return userType;
		}

		protected void setUserType(String userType) {
			this.userType = userType;
		}

		protected Double getComplianceRate() {
			return complianceRate;
		}

		protected void setComplianceRate(Double complianceRate) {
			this.complianceRate = complianceRate;
		}
	}
}
