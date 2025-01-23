package com.redbox.global.util.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSender {

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final JavaMailSender javaMailSender;

    public void sendMail(String receivedMail, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(MimeMessage.RecipientType.TO, receivedMail);
            message.setSubject(subject);
            message.setFrom(senderEmail);
            message.setText(content, "utf-8", "html");

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new EmailCreationException();
        } catch (MailException e) {
            throw new EmailSendException();
        }
    }
}
