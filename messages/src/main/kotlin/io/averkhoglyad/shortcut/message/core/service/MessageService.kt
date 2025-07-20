package io.averkhoglyad.shortcut.message.core.service

import io.averkhoglyad.shortcut.common.data.EntityResult
import io.averkhoglyad.shortcut.common.persistence.transaction.transaction
import io.averkhoglyad.shortcut.message.core.converter.MessageConverter
import io.averkhoglyad.shortcut.message.core.model.Message
import io.averkhoglyad.shortcut.message.core.model.MessageRequest
import io.averkhoglyad.shortcut.message.core.model.UserRef
import io.averkhoglyad.shortcut.message.core.output.EventPublisher
import io.averkhoglyad.shortcut.message.core.persistence.entity.ChatEntity
import io.averkhoglyad.shortcut.message.core.persistence.entity.MessageEntity
import io.averkhoglyad.shortcut.message.core.persistence.repository.MessageRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface MessageService {

    fun listForChat(chatId: UUID, pageSize: Int): List<Message>

    fun listForChat(chatId: UUID, afterId: UUID, pageSize: Int): List<Message>

    fun create(request: MessageRequest, author: UserRef): EntityResult<Message>

}

@Service
class MessageServiceImpl(
    private val repository: MessageRepository,
    private val converter: MessageConverter,
    private val eventPublisher: EventPublisher<Message>,
) : MessageService {

    @Transactional(readOnly = true)
    override fun listForChat(chatId: UUID, pageSize: Int): List<Message> {
        val chat = AggregateReference.to<ChatEntity, UUID>(chatId)
        return repository.findByChatOrderByIdDesc(chat, Pageable.ofSize(pageSize))
            .map { it.toModel() }
    }

    @Transactional(readOnly = true)
    override fun listForChat(chatId: UUID, afterId: UUID, pageSize: Int): List<Message> {
        val chat = AggregateReference.to<ChatEntity, UUID>(chatId)
        return repository.findByChatAndIdLessThanOrderByIdDesc(chat, afterId, Pageable.ofSize(pageSize))
            .map { it.toModel() }
    }

    @Transactional
    override fun create(request: MessageRequest, author: UserRef): EntityResult<Message> {
        return repository.save(request.toEntity(author))
            .toModel()
            .also { emitMessageCreatedLifecycleEvent(it) }
            .let { EntityResult.Success(it) }
    }

    private fun emitMessageCreatedLifecycleEvent(message: Message) {
        transaction {
            afterCommit {
                eventPublisher.publish(message)
            }
        }
    }

    private fun MessageRequest.toEntity(author: UserRef) = converter.toEntity(this, author)

    private fun MessageEntity.toModel() = converter.fromEntity(this)
}

