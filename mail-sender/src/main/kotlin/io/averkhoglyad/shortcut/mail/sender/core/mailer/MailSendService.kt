package io.averkhoglyad.shortcut.mail.sender.core.mailer

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailSender
import org.springframework.stereotype.Service

@Service
class MailSendService(
    private val emailSender: MailSender,
    private val messageFactory: MailMessageFactory,
    @Value("\${sender.from}")
    private val from: String
) {

    fun send(message: EmailMessage) {
        messageFactory.create(from, message)
            .let { emailSender.send(it) }
    }
}