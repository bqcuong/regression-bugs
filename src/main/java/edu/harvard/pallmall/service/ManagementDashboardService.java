package edu.harvard.pallmall.service;

import edu.harvard.pallmall.domain.admin.Email;
import edu.harvard.pallmall.domain.core.Event;

/**
 * The ManagementDashboardService...
 */
public interface ManagementDashboardService {

    // Sends Email Notifications
    void sendEmail(Email email);

}
