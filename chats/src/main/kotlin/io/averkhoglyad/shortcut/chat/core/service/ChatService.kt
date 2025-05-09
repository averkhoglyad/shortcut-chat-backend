package io.averkhoglyad.shortcut.chat.core.service

import io.averkhoglyad.shortcut.chat.core.converter.ChatConverter
import io.averkhoglyad.shortcut.chat.core.model.ChatCreatedEvent
import io.averkhoglyad.shortcut.chat.core.model.ChatDetails
import io.averkhoglyad.shortcut.chat.core.model.ChatListItem
import io.averkhoglyad.shortcut.chat.core.model.ChatMembers
import io.averkhoglyad.shortcut.chat.core.model.ChatRequest
import io.averkhoglyad.shortcut.chat.core.model.UserRef
import io.averkhoglyad.shortcut.chat.core.persistence.entity.ChatEntity
import io.averkhoglyad.shortcut.chat.core.persistence.repository.ChatRepository
import io.averkhoglyad.shortcut.chat.core.service.message.MessageFactory
import io.averkhoglyad.shortcut.chat.outbox.OutboxService
import io.averkhoglyad.shortcut.common.data.EntityResult
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface ChatService {

    fun listForUser(userId: UUID, pageSize: Int): List<ChatListItem>

    fun listForUser(userId: UUID, afterId: UUID, pageSize: Int): List<ChatListItem>

    fun findById(chatId: UUID): EntityResult<ChatDetails>

    fun create(chat: ChatRequest): EntityResult<ChatDetails>

    fun findMembersByIds(chatIds: Collection<UUID>): List<ChatMembers>
}

@Service
class ChatServiceImpl(
    private val converter: ChatConverter,
    private val repository: ChatRepository,
    private val outboxService: OutboxService,
    private val chatCreatedMessageFactory: MessageFactory<ChatDetails, ChatCreatedEvent>,
) : ChatService {

    override fun listForUser(userId: UUID, pageSize: Int): List<ChatListItem> {
        return repository.findByMembersContainsOrderById(userId, Pageable.ofSize(pageSize))
            .map { it.toListItem() }
    }

    override fun listForUser(userId: UUID, afterId: UUID, pageSize: Int): List<ChatListItem> {
        return repository.findByMembersContainsAndIdGreaterThanOrderById(userId, afterId, Pageable.ofSize(pageSize))
            .map { it.toListItem() }
    }

    override fun findById(chatId: UUID): EntityResult<ChatDetails> {
        return repository.findById(chatId)
            ?.toDetails()
            ?.let { EntityResult.Success(it) }
            ?: EntityResult.NotFound
    }

    @Transactional
    override fun create(chat: ChatRequest): EntityResult<ChatDetails> {
        return repository.save(chat.toEntity())
            .toDetails()
            .also { emitChatCreatedLifecycleEvent(it) }
            .let { EntityResult.Success(it) }
    }

    override fun findMembersByIds(chatIds: Collection<UUID>): List<ChatMembers> {
        return repository.findMembersByChatIdIsIn(chatIds)
            .groupBy { it.chat.id }
            .map { (chatId, entity) ->
                ChatMembers(
                    id = requireNotNull(chatId),
                    members = entity.map { UserRef(requireNotNull(it.user.id)) }
                )
            }
    }

    private fun emitChatCreatedLifecycleEvent(chat: ChatDetails) {
        outboxService.save(chatCreatedMessageFactory.create(chat))
    }

    private fun ChatRequest.toEntity(): ChatEntity = converter.toEntity(this)

    private fun ChatEntity.toDetails(): ChatDetails = converter.toDetails(this)

    private fun ChatEntity.toListItem(): ChatListItem = converter.toListItem(this)
}

