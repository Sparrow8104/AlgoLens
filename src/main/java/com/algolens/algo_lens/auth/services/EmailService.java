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

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

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

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = frontendBaseUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("AlgoLens — Reset Your Password");
        message.setText("""
            Hi,

            We received a request to reset your AlgoLens password.
            Click the link below to choose a new password (expires in 15 minutes):

            %s

            If you did not request this, you can safely ignore this email.
            Your password will not change unless you click the link above.

            — The AlgoLens Team
            """.formatted(resetLink));

        mailSender.send(message);
    }


}
