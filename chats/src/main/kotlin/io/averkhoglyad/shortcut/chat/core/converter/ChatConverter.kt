package io.averkhoglyad.shortcut.chat.core.converter

import io.averkhoglyad.shortcut.chat.core.model.ChatDetails
import io.averkhoglyad.shortcut.chat.core.model.ChatListItem
import io.averkhoglyad.shortcut.chat.core.model.ChatRequest
import io.averkhoglyad.shortcut.chat.core.model.User
import io.averkhoglyad.shortcut.chat.core.model.UserRef
import io.averkhoglyad.shortcut.chat.core.persistence.entity.ChatEntity
import io.averkhoglyad.shortcut.chat.core.persistence.entity.MemberEntity
import io.averkhoglyad.shortcut.chat.core.persistence.entity.UserEntity
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

interface ChatConverter {

    fun toEntity(chat: ChatRequest): ChatEntity

    fun toEntity(chat: ChatRequest, entity: ChatEntity): ChatEntity

    fun toDetails(entity: ChatEntity): ChatDetails

    fun toListItem(entity: ChatEntity): ChatListItem

}

@Component
class ChatConverterImpl : ChatConverter {

    override fun toEntity(chat: ChatRequest): ChatEntity {
        return toEntity(chat, ChatEntity())
    }

    override fun toEntity(chat: ChatRequest, entity: ChatEntity): ChatEntity {
        return entity
            .apply {
                this.name = chat.name
                this.members = this.members.mergeWith(chat.members).toSet()
            }
    }

    override fun toListItem(entity: ChatEntity): ChatListItem {
        return ChatListItem(
            id = requireNotNull(entity.id),
            name = entity.name,
            owner = null, // TODO:
            createdAt = entity.createdAt,
        )
    }

    override fun toDetails(entity: ChatEntity): ChatDetails {
        return ChatDetails(
            id = requireNotNull(entity.id),
            name = entity.name,
            owner = null, // TODO:
            members = emptySet(), // TODO:
            createdAt = entity.createdAt,
        )
    }
}


private fun Collection<MemberEntity>.mergeWith(other: Collection<UserRef>): Collection<MemberEntity> {
    val result = arrayListOf<MemberEntity>()

    this.filter { el -> other.any { it.id == el.user.id } }
        .toCollection(result)

    other.filterNot { el -> this.any { it.user.id == el.id } }
        .map {
            MemberEntity()
                .apply {
                    this.user = it.id.asRef()
                    this.createdAt = Instant.now()
                }
        }
        .toCollection(result)

    return result
}

private fun UUID.asRef() = AggregateReference.to<UserEntity, UUID>(this)
