package com.pluralsight.conference.util;

import com.pluralsight.conference.model.Password;
import com.pluralsight.conference.service.PasswordService;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PasswordListener implements ApplicationListener<OnPasswordResetEvent> {

    private final JavaMailSender mailSender;

    private final PasswordService passwordService;

    public PasswordListener(JavaMailSender mailSender, PasswordService passwordService) {
        this.mailSender = mailSender;
        this.passwordService = passwordService;
    }

    @Override
    public void onApplicationEvent(OnPasswordResetEvent event) {
        this.resetPassword(event);
    }

    private void resetPassword(OnPasswordResetEvent event) {
        Password password = event.getPassword();
        String token = UUID.randomUUID().toString();
        passwordService.createResetToken(password, token);

        String recipientAddress = password.getEmail();
        String subject = "Reset Password";
        String confirmationUrl = event.getAppUrl() + "/passwordReset?token=" + token;
        String message = "Reset password:";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        String serverUrl = "http://localhost:8080";
        email.setText(message + "\r\n" + serverUrl + confirmationUrl);
        mailSender.send(email);
    }
}
