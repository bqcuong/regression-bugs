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
import com.opencsv.bean.ColumnPositionMappingStrategy;
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
	
	private Writer stringWriterReport(Map<String, Double> data) {
		
		Writer writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
		
		data.forEach((k,v) -> csvWriter.writeNext(new String[] {(String)k,((Double)v).toString()}));
		
		return writer;
	}
	
	/*
	 * https://stackoverflow.com/questions/43326129/how-to-write-bean-to-csv-using-opencsv-without-converting-to-string
	 */
	@SuppressWarnings("unchecked")
	private Writer beanWriterReport(Map<String, Double> data) {
		Writer writer = new StringWriter();
		
		@SuppressWarnings("rawtypes")
		ColumnPositionMappingStrategy mappingStrategy =
	            new ColumnPositionMappingStrategy();
	    mappingStrategy.setType(MyReport.class);
	    
	    @SuppressWarnings("rawtypes")
		StatefulBeanToCsvBuilder<MyReport> builder = new StatefulBeanToCsvBuilder(writer);
	    @SuppressWarnings("rawtypes")
		StatefulBeanToCsv beanWriter = builder
	              .withMappingStrategy(mappingStrategy)
	              .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
	              .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
	              .build();

	    List<MyReport> myReports = new ArrayList<>();
	    data.forEach((k,v) -> myReports.add(new MyReport(k,v)));
	    
	    try {
			beanWriter.write(myReports);
		} catch (CsvDataTypeMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CsvRequiredFieldEmptyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return writer;
	}
	

	@Override
	public String createReport() {
				
		Map<String, Double> complianceData = new HashMap<String, Double>();
		complianceData.put("abc", 1.3);
		complianceData.put("dec", 1.3);
		complianceData.put("ghc", 1.3);
		
//		Writer writer = stringWriterReport(complianceData);
		Writer writer = beanWriterReport(complianceData);
		
		
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
