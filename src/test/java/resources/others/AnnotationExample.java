package resources.others;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import com.gdssecurity.pmd.annotations.SQLSink;

public class AnnotationExample {

	@SQLSink
	public  void test1(JspWriter out, String selected) throws IOException {
		out.write(selected); // No warning because is sink
	}
	
	
}
