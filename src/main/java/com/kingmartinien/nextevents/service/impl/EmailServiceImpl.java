package com.kingmartinien.nextevents.service.impl;

import com.kingmartinien.nextevents.enums.EmailTemplateName;
import com.kingmartinien.nextevents.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendEmail(String to, String username, String subject, String confirmationUrl, String activationCode, EmailTemplateName emailTemplateName) throws MessagingException {
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name());

        Map<String, Object> properties = new HashMap<>();
        properties.put("email", to);
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activationCode", activationCode);

        Context context = new Context();
        context.setVariables(properties);

        mimeMessageHelper.setFrom("contact@nextevents.com");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setSentDate(new Date());

        String template = templateEngine.process(emailTemplateName.getName(), context);
        mimeMessageHelper.setText(template, true);

        this.javaMailSender.send(mimeMessage);

    }
}
