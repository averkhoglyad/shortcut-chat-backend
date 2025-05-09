package io.averkhoglyad.shortcut.message.core.output

import io.averkhoglyad.shortcut.message.core.model.Message
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

interface EventPublisher<E> {

    fun publish(event: E)

}

@Component
class ChatLifecycleEventPublisher(
    private val recordFactory: ProducerRecordFactory<Message, ProducerRecord<String, Any>>,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) : EventPublisher<Message> {

    override fun publish(event: Message) {
        val record = recordFactory.create(event)
        kafkaTemplate.send(record)
    }
}
