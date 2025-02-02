package io.averkhoglyad.shortcut.mail.sender.api

import io.averkhoglyad.shortcut.mail.sender.core.mailer.EmailMessage
import io.averkhoglyad.shortcut.mail.sender.core.service.MailSendService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class SendCommandHandler(
    private val service: MailSendService
) {

    @KafkaListener(
        topics = ["MailerCommands"],
        idIsGroup = false,
        properties = [
            "spring.json.value.default.type=io.averkhoglyad.shortcut.mail.sender.core.mailer.EmailMessage"
        ]
    )
    fun handleTaskCreated(@Header("X-Event-Id") eventId: String, message: EmailMessage) {
        service.send(eventId, message)
    }
}