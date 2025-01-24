package com.redbox.global.util.email

import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class EmailSender(
    private val javaMailSender: JavaMailSender,
    @Value("\${spring.mail.username}") private val senderEmail: String
) {
    fun sendMail(receivedMail: String, subject: String, content: String) {
        try {
            val message = javaMailSender.createMimeMessage().apply {
                addRecipients(MimeMessage.RecipientType.TO, receivedMail)
                setSubject(subject)
                setFrom(senderEmail)
                setText(content, "utf-8", "html")
            }
            javaMailSender.send(message)
        } catch (e: MessagingException) {
            throw EmailCreationException()
        } catch (e: MailException) {
            throw EmailSendException()
        }
    }
}