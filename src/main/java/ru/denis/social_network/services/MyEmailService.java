package ru.denis.social_network.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MyEmailService {
    @Value("${app.email-confirmation.url}")
    private String confirmationUrl;

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String token) {
        String confirmationLink = confirmationUrl + "?token=" + token;
        System.out.println(confirmationLink);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setFrom("kadirovdenis0@gmail.com");
        message.setSubject("Register confirmation");
        message.setText("To complete the registration, please click the link below: " + confirmationLink);
        System.out.println(message.toString());
        mailSender.send(message);
    }
}
