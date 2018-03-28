package edu.harvard.h2ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

/**
 * H2MS Rest Application
 * @version 1.0
 */
@SpringBootApplication
public class H2MSRestAppInitializer {

	private static final Logger log = LoggerFactory.getLogger(H2MSRestAppInitializer.class);
	@SuppressWarnings("unused")
	
	public static void main(String[] args) {
		GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
		SpringApplication.run(H2MSRestAppInitializer.class, args);
	}
}
