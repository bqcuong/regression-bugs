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
    log.info("notification Name: " + notification.getName());
    log.info("notification subscribers: " + notification.getUser());

    for (User user : notification.getUser()) {

      log.info("Evaluating user" + user.getEmail());

      if (isTimeToNotify(notification, user)) {
        log.info("user " + user.getEmail() + " is ready to be notified");

        //        user.getEmail();

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

        log.info("email sent " + message);

        log.info("before reset" + notification.getEmailLastNotifiedTimes().get(user.getEmail()));
        // finally, not the time in which the last email was sent for the user
        NotificationServiceImpl.resetEmailLastNotifiedTime(notification, user);

        log.info("after reset" + notification.getEmailLastNotifiedTimes().get(user.getEmail()));

      } else {
        log.info("user " + user.getEmail() + " is notready to be notified");
      }
    }
  }

  /** @return current unix time */
  private static long getUnixTime() {

    return System.currentTimeMillis() / 1000L;
  }

  /**
   * Sets the user notification time to current time
   *
   * @param notification
   * @param user
   */
  private static void resetEmailLastNotifiedTime(Notification notification, User user) {

    notification.setEmailLastNotifiedTime(user.getEmail(), getUnixTime());
  }

  /**
   * Determines whether user should be notified or not based on requested user notfication interval
   *
   * <p>Formula: dNT = currentTime - lastNotificationtime Business Rule: dNT > interval time =>
   * notify default => do not notify
   *
   * @param notification
   * @param user
   * @return
   */
  private static boolean isTimeToNotify(Notification notification, User user) {
    String userEmail = user.getEmail();
    long lastNotificationTime = notification.getEmailLastNotifiedTimes().get(userEmail);

    // TODO: have interval interpretation mechanism based on user interval preference
    long interval = 1000L;

    long currentTime = getUnixTime();

    long deltaNotificationTime = currentTime - lastNotificationTime;

    if (deltaNotificationTime > interval) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Adds user to notification's subscription list
   *
   * @param user
   * @param notification
   */
  public void subscribeUserNotification(User user, Notification notification) {

    notification.addUser(user);
    resetEmailLastNotifiedTime(notification, user);
  }
}
