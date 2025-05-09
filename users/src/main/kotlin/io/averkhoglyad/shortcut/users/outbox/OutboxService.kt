package io.averkhoglyad.shortcut.users.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import io.averkhoglyad.shortcut.users.core.persistence.entity.OutboxMessageEntity
import io.averkhoglyad.shortcut.users.core.persistence.repository.OutboxMessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface OutboxService {

    fun saveMessage(message: OutboxMessage<*>)

}

@Service
class OutboxServiceImpl(
    private val repository: OutboxMessageRepository,
    private val objectMapper: ObjectMapper // Probably could be replaced with Kafka Serializer or at least delegated to it
) : OutboxService {

    @Transactional
    override fun saveMessage(message: OutboxMessage<*>) {
        val entity = OutboxMessageEntity()
            .apply {
                type = message.type
                version = message.version
                body = objectMapper.writeValueAsBytes(message.body)
            }

        repository.save(entity)
    }
}
