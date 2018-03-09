package edu.harvard.pallmall.service;

import edu.harvard.pallmall.domain.admin.Email;
import edu.harvard.pallmall.domain.core.Event;
import edu.harvard.pallmall.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * The Management Dashboard Service Implementor...
 */
@Service("managementDashboardService")
@Repository
@Transactional
public class ManagementDashboardServiceImpl implements ManagementDashboardService {

    /**
     *
     * @param email
     */
    public void sendEmail(Email email) {
        //fixme requires additional configuration
        String host = "localhost";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email.getFrom()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getTo()));
            message.setSubject(email.getSubject());
            message.setText(email.getText());
            // Sends message
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }


}
