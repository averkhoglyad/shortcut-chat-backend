package io.averkhoglyad.shortcut.users.core.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.averkhoglyad.shortcut.users.core.model.Message
import io.averkhoglyad.shortcut.users.core.persistence.entity.MessageOutboxEntity
import io.averkhoglyad.shortcut.users.core.persistence.repository.MessageOutboxRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface OutboxService {

    fun saveMessage(message: Message<*>)

}

@Service
class OutboxServiceImpl(
    private val repository: MessageOutboxRepository,
    private val objectMapper: ObjectMapper // Probably could be replaced with Kafka Serializer or at least delegated to it
) : OutboxService {

    @Transactional
    override fun saveMessage(message: Message<*>) {
        val entity = MessageOutboxEntity()
            .apply {
                type = message.type
                version = message.version
                body = objectMapper.writeValueAsBytes(message.body)
            }

        repository.save(entity)
    }
}
