package com.algolens.algo_lens.auth.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmailVerification(String toMail,String token) {
        String verificationLink=baseUrl+"/api/auth/verify-email?token="+token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toMail);
        message.setSubject("Verify your algolens account");
        message.setText(                "Welcome to AlgoLens!\n\n" +
                "Click the link below to verify your email address.\n" +
                "This link expires in 24 hours.\n\n" +
                verificationLink + "\n\n" +
                "If you didn't create this account, you can ignore this email."
        );
        mailSender.send(message);
    }



}
