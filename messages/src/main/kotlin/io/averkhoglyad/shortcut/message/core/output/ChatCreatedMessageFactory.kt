package io.averkhoglyad.shortcut.message.core.output

import io.averkhoglyad.shortcut.message.core.model.Message
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Headers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Clock
import java.util.*

@Component
class MessageCreatedMessageFactoryImpl(
    @Value("ChatLifecycle")
    private val topic: String,
    @Value("v1")
    private val version: String,
    private val eventName: String,
    @Value("\${spring.application.name}")
    private val applicationName: String,
    private val clock: Clock,
) : ProducerRecordFactory<Message, ProducerRecord<String, Any>> {

    @Autowired
    constructor(
        @Value("ChatLifecycle") topic: String,
        @Value("MessagePublished") eventName: String,
        @Value("v1") version: String,
        @Value("\${spring.application.name}") applicationName: String,
    ) : this(topic, version, eventName, applicationName, Clock.systemUTC())


    override fun create(event: Message): ProducerRecord<String, Any> =
        ProducerRecord<String, Any>(topic, event.chat.id.toString(), event)
            .apply {
                headers()
                    .addAsString(EVENT_ID, UUID.randomUUID())
                    .addAsString(EVENT_NAME, eventName)
                    .addAsString(EVENT_VERSION, version)
                    .addAsString(PUBLISHED_AT, clock.instant())
                    .addAsString(PUBLISHED_BY, applicationName)
            }
}

// TODO: Must be moved to common lib with other infrastructure aware code, configs, constants
private const val EVENT_ID = "X-Event-Id"
private const val EVENT_NAME = "X-Event-Name"
private const val EVENT_VERSION = "X-Event-Version"
private const val PUBLISHED_AT = "X-Published-At"
private const val PUBLISHED_BY = "X-Published-By"

private fun Headers.addAsString(name: String, value: String): Headers =
    add(name, value.toByteArray(Charsets.UTF_8))

private fun Headers.addAsString(name: String, value: Any): Headers =
    addAsString(name, value.toString())
