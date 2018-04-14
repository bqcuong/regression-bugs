package edu.harvard.h2ms.service;

import edu.harvard.h2ms.domain.core.Notification;
import edu.harvard.h2ms.domain.core.User;
import edu.harvard.h2ms.repository.NotificationRepository;
import edu.harvard.h2ms.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service("notificationService")
public class NotificationServiceImpl {

  private static final Log log = LogFactory.getLog(NotificationServiceImpl.class);

  @Autowired private NotificationRepository notificationRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private EmailService emailService;

  @Scheduled(fixedRate = 10000)
  public void pollNotifications() {
    log.info("****polling notifications");

    for (Notification notification : notificationRepository.findAll()) {

      this.notifyUsers(notification);
    }
  }

  private void notifyUsers(Notification notification) {
    log.info("notification Name" + notification.getName());

    for (User user : notification.getUser()) {
      // TODO: add intervals
      user.getEmail();

      SimpleMailMessage message = new SimpleMailMessage();

      // user email address
      message.setTo(user.getEmail());

      // uncomment for quick test:
      // message.setTo("my.email.address@gmail.com");

      message.setSubject(notification.getNotificationTitle());

      String messageText = notification.getNotificationBody();

      message.setText(messageText);

      // actually send the message
      emailService.sendEmail(message);
    }
  }

  public void subscribeUserNotification(String userEmail, String notificationName) {

    Notification notification = notificationRepository.findOneByName(notificationName);
  }
}
