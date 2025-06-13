package io.averkhoglyad.shortcut.chat.core.service

import io.averkhoglyad.shortcut.chat.core.converter.ChatConverter
import io.averkhoglyad.shortcut.chat.core.model.ChatCreatedEvent
import io.averkhoglyad.shortcut.chat.core.model.ChatDetails
import io.averkhoglyad.shortcut.chat.core.model.ChatListItem
import io.averkhoglyad.shortcut.chat.core.model.ChatMembers
import io.averkhoglyad.shortcut.chat.core.model.ChatRequest
import io.averkhoglyad.shortcut.chat.core.model.UserRef
import io.averkhoglyad.shortcut.chat.core.persistence.entity.ChatEntity
import io.averkhoglyad.shortcut.chat.core.persistence.entity.UserEntity
import io.averkhoglyad.shortcut.chat.core.persistence.repository.ChatRepository
import io.averkhoglyad.shortcut.chat.core.persistence.repository.UserRepository
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
    private val userRepository: UserRepository,
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
            ?.let { it to userRepository.findByIdIsIn(it.memberIds) }
            ?.let { (entity, members) -> entity.toDetails(members) }
            ?.let { EntityResult.Success(it) }
            ?: EntityResult.NotFound
    }

    @Transactional
    override fun create(chat: ChatRequest): EntityResult<ChatDetails> {
        return repository.save(chat.toEntity())
            .let { it to userRepository.findByIdIsIn(it.memberIds) }
            .let { (entity, members) -> entity.toDetails(members) }
            .also { emitChatCreatedLifecycleEvent(it) }
            .let { EntityResult.Success(it) }
    }

    override fun findMembersByIds(chatIds: Collection<UUID>): List<ChatMembers> {
        if (chatIds.isEmpty()) {
            return emptyList()
        }
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

    private fun ChatEntity.toDetails(memberUsers: Collection<UserEntity>): ChatDetails =
        converter.toDetails(this, memberUsers)

    private fun ChatEntity.toListItem(): ChatListItem = converter.toListItem(this)
}

private val ChatEntity.memberIds: Collection<UUID>
    get() = members.mapNotNull { it.user.id }
