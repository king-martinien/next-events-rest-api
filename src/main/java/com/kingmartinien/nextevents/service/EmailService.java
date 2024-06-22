package com.kingmartinien.nextevents.service;

import com.kingmartinien.nextevents.enums.EmailTemplateName;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    @Async
    void sendEmail(
            String to,
            String username,
            String subject,
            String confirmationUrl,
            String activationCode,
            EmailTemplateName emailTemplateName
    ) throws MessagingException;

}
