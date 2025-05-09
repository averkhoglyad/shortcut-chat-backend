package io.averkhoglyad.shortcut.chat.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import io.averkhoglyad.shortcut.chat.core.persistence.entity.OutboxMessageEntity
import io.averkhoglyad.shortcut.chat.core.persistence.repository.OutboxMessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Propagation.MANDATORY
import org.springframework.transaction.annotation.Transactional

interface OutboxService {

    fun save(message: OutboxMessage<*>)

}

@Service
class OutboxServiceImpl(
    private val repository: OutboxMessageRepository,
    private val objectMapper: ObjectMapper // TODO: probably could be replaced with Kafka Serializer or at least delegated to it
) : OutboxService {

    @Transactional(propagation = MANDATORY)
    override fun save(message: OutboxMessage<*>) {
        val entity = OutboxMessageEntity()
            .apply {
                this.type = message.type
                this.version = message.version
                this.key = message.key
                this.body = objectMapper.writeValueAsBytes(message.body)
            }
        repository.save(entity)
    }
}
