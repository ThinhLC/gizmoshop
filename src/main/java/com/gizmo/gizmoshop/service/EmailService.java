    package com.gizmo.gizmoshop.service;

    import jakarta.mail.MessagingException;
    import jakarta.mail.internet.MimeMessage;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.mail.SimpleMailMessage;
    import org.springframework.mail.javamail.JavaMailSender;
    import org.springframework.mail.javamail.MimeMessageHelper;
    import org.springframework.stereotype.Service;


    @Service
    public class EmailService {

        private final JavaMailSender mailSender;

        @Autowired
        public EmailService(JavaMailSender mailSender) {
            this.mailSender = mailSender;
        }

        public void sendOtpEmail(String to, String otp) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Gizmoshop - Support");
            message.setText("Your OTP is: " + otp);
            mailSender.send(message);
        }

    }
