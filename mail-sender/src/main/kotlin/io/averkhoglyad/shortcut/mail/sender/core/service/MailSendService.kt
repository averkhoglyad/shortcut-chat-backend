package io.averkhoglyad.shortcut.mail.sender.core.service

import io.averkhoglyad.shortcut.mail.sender.core.mailer.EmailMessage
import io.averkhoglyad.shortcut.mail.sender.core.mailer.MailMessageFactory
import io.averkhoglyad.shortcut.mail.sender.core.persistence.MessageEntity
import io.averkhoglyad.shortcut.mail.sender.core.persistence.MessageRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailSender
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant

@Service
class MailSendService(
    private val repository: MessageRepository,
    private val emailSender: MailSender,
    private val messageFactory: MailMessageFactory,
    @Value("\${sender.from}")
    private val from: String,
) {

    fun send(eventId: String, message: EmailMessage) {
        require(message.to.isNotEmpty())
        handleCommand(eventId, message) {
            messageFactory.create(from, message)
                .let { emailSender.send(it) }
        }
    }

    private fun handleCommand(eventId: String, message: EmailMessage, block: () -> Unit) {
        val entity = repository.findById(eventId)
            ?: createMessageEntity(eventId, message)

        if (entity.status == MessageEntity.Status.COMPLETED) {
            return
        }

        try {
            block()

            entity.copy(payload = message)
                .completed()
                .also { repository.save(it) }
        } catch (e: Exception) {
            entity.copy(payload = message)
                .failed(e)
                .also { repository.save(it) }
        }
    }

    private fun createMessageEntity(eventId: String, message: EmailMessage) = MessageEntity(
        id = eventId,
        payload = message,
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
        status = MessageEntity.Status.CREATED
    )
}

private fun MessageEntity.completed(): MessageEntity =
    copy(
        status = MessageEntity.Status.COMPLETED,
        updatedAt = Instant.now(),
    )

private fun MessageEntity.failed(e: Exception): MessageEntity =
    copy(
        status = MessageEntity.Status.FAILED,
        exception = e,
        updatedAt = Instant.now(),
    )