package edu.harvard.h2ms.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service("notificationService")
public class NotificationServiceImpl {
	
	 private static final Log log = LogFactory.getLog(NotificationServiceImpl.class);
	 
	 @Scheduled(fixedRate = 10000)
	 public void pollNotifications() {
		 log.info("****polling notifications");
	 }
	

}

