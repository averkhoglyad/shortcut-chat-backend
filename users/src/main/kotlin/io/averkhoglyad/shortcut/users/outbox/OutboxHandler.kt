package io.averkhoglyad.shortcut.users.outbox

import io.averkhoglyad.shortcut.users.core.persistence.entity.OutboxMessageEntity
import io.averkhoglyad.shortcut.users.core.persistence.repository.OutboxMessageRepository
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Headers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.Clock

// TODO: Must be moved to some common transactional-outbox lib
interface MessageOutboxHandler {

    fun handleNext()

}

@Component
class MessageOutboxHandlerImpl(
    private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
    private val repository: OutboxMessageRepository,
    @Value("\${scheduler.message-outbox-handler.portion-size:128}")
    private val portionSize: Int,
    @Value("\${spring.application.name}")
    private val applicationName: String,
    private val clock: Clock
) : MessageOutboxHandler {

    private val pageable = Pageable.ofSize(portionSize)

    @Autowired
    constructor(
        kafkaTemplate: KafkaTemplate<String, ByteArray>,
        repository: OutboxMessageRepository,
        @Value("\${scheduler.message-outbox-handler.portion-size:128}")
        portionSize: Int,
        @Value("\${spring.application.name}")
        applicationName: String
    ): this(kafkaTemplate, repository, portionSize, applicationName, Clock.systemUTC())

    override fun handleNext() {
        whileHasPortionToProcess { portion ->
            portion.onEach { sendMessage(it) }
                .mapNotNull { it.id }
                .let { repository.markAsPublishedByIdIsIn(it) }
        }
    }

    private fun whileHasPortionToProcess(block: (List<OutboxMessageEntity>) -> Unit) {
        val now = clock.instant()
        generateSequence { repository.findByCreatedAtLessThanAndPublishedAtIsNullOrderByCreatedAt(now, pageable) }
            .takeWhile { it.isNotEmpty() }
            .forEach { block.invoke(it) }
    }

    private fun sendMessage(message: OutboxMessageEntity) {
        val topic = detectTargetTopic(message.type)
        val record = ProducerRecord<String, ByteArray>(topic, message.body)
            .withHeadersFor(message)
        kafkaTemplate.send(record)
    }

    // TODO: Must be configurable!
    private fun detectTargetTopic(messageType: String): String = when (messageType) {
        "SendCreatedUserNotification" -> "MailerCommands"
        "UserCreated" -> "UserLifecycle"
        else -> error("Unexpected message type $messageType")
    }

    // TODO: Must be configurable!
    private fun convertEventName(messageType: String): String = when (messageType) {
        "SendCreatedUserNotification" -> "SendEmail"
        else -> messageType
    }

    private fun <B> ProducerRecord<String, B>.withHeadersFor(message: OutboxMessageEntity): ProducerRecord<String, B> {
        return this.apply {
            headers()
                .addAsString(EVENT_ID, message.id)
                .addAsString(EVENT_NAME, convertEventName(message.type))
                .addAsString(EVENT_VERSION, message.version)
                .addAsString(PUBLISHED_AT, clock.instant())
                .addAsString(PUBLISHED_BY, applicationName)
        }
    }
}

// TODO: Must be moved to common lib with other infrastructure aware code, configs, constants
const val EVENT_ID = "X-Event-Id"
const val EVENT_NAME = "X-Event-Name"
const val EVENT_VERSION = "X-Event-Version"
const val PUBLISHED_AT = "X-Published-At"
const val PUBLISHED_BY = "X-Published-By"

fun Headers.addAsString(name: String, value: String): Headers =
    add(name, value.toByteArray(Charsets.UTF_8))

fun Headers.addAsString(name: String, value: Any?): Headers =
    addAsString(name, value.toString())
