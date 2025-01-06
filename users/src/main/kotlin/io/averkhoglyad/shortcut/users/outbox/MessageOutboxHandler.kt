package io.averkhoglyad.shortcut.users.outbox

import io.averkhoglyad.shortcut.users.core.persistence.entity.MessageOutboxEntity
import io.averkhoglyad.shortcut.users.core.persistence.repository.MessageOutboxRepository
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Headers
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.Period

// TODO: Must be moved to some common transactional-outbox lib
interface MessageOutboxHandler {

    fun cleanOld()
    fun handleNext()

}

@Component
class MessageOutboxHandlerImpl(
    private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
    private val messageOutboxRepository: MessageOutboxRepository,
    @Value("\${scheduler.message-outbox-handler.portion-size:128}")
    private val portionSize: Int,
    @Value("\${spring.application.name}")
    private val applicationName: String,
    @Value("\${scheduler.message-outbox-cleaner.valid-period}")
    private val validPeriod: Period
): MessageOutboxHandler {

    private val pageable = Pageable.ofSize(portionSize)

    override fun handleNext() {
        var portion = emptyList<MessageOutboxEntity>()
        do {
            portion = messageOutboxRepository.findByPublishedAtIsNullOrderByCreatedAt(pageable)
            portion.forEach { sendMessage(it) }
            portion.mapNotNull { it.id }
                .takeIf { it.isNotEmpty() }
                ?.let { messageOutboxRepository.markAsPublishedByIdIsIn(it) }
        } while (portion.isNotEmpty())
    }

    private fun sendMessage(message: MessageOutboxEntity) {
        val topic = detectTargetTopic(message.type)
        val record = ProducerRecord<String, ByteArray>(topic, message.body)
        record.headers().addAsString(EVENT_ID, message.id)
        record.headers().addAsString(EVENT_NAME, convertEventName(message.type))
        record.headers().addAsString(EVENT_VERSION, message.version)
        record.headers().addAsString(PUBLISHED_AT, Instant.now())
        record.headers().addAsString(PUBLISHED_BY, applicationName)
        kafkaTemplate.send(record)
    }

    private fun detectTargetTopic(messageType: String): String {
        // TODO: Must be configurable!
        return when (messageType) {
            "SendCreatedUserNotification" -> "MailerCommands"
            "UserCreated" -> "UserLifecycle"
            else -> error("Unexpected message type $messageType")
        }
    }

    // TODO: Must be configurable!
    private fun convertEventName(messageType: String): String {
        return when (messageType) {
            "SendCreatedUserNotification" -> "SendEmail"
            else -> messageType
        }
    }

    override fun cleanOld() {
        messageOutboxRepository.deleteByPublishedAtLessThan(Instant.now() - validPeriod)
    }
}

// TODO: Must be moved to common lib with other infrastructure aware code, configs, constants
const val EVENT_ID = "X-Event-Id"
const val EVENT_NAME = "X-Event-Name"
const val EVENT_VERSION = "X-Event-Version"
const val PUBLISHED_AT = "X-Published-At"
const val PUBLISHED_BY = "X-Published-By"

private fun Headers.addAsString(name: String, value: String) = add(name, value.toByteArray(Charsets.UTF_8))

private fun Headers.addAsString(name: String, value: Any?) = addAsString(name, value.toString())
