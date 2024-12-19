package com.beaconfire.email_service.Service;

import javax.mail.MessagingException;

public interface EmailService {
    void sendEmail(String recipientEmail, String firstname, String lastname, String url) throws MessagingException;
}
