package io.averkhoglyad.shortcut.message.core.converter

import io.averkhoglyad.shortcut.message.core.model.ChatRef
import io.averkhoglyad.shortcut.message.core.model.Message
import io.averkhoglyad.shortcut.message.core.model.MessageRequest
import io.averkhoglyad.shortcut.message.core.model.UserRef
import io.averkhoglyad.shortcut.message.core.persistence.entity.MessageEntity
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.stereotype.Component

interface MessageConverter {

    fun toEntity(message: MessageRequest, author: UserRef): MessageEntity

    fun fromEntity(entity: MessageEntity): Message

}

@Component
class MessageConverterImpl : MessageConverter {

    override fun toEntity(message: MessageRequest, author: UserRef): MessageEntity =
        MessageEntity()
            .apply {
                this.text = message.text
                this.author = AggregateReference.to(author.id)
                this.chat = AggregateReference.to(message.chat.id)
            }

    override fun fromEntity(entity: MessageEntity): Message =
        Message(
            id = requireNotNull(entity.id),
            text = entity.text,
            chat = ChatRef(requireNotNull(entity.chat.id)),
            author = UserRef(requireNotNull(entity.author.id)),
            createdAt = entity.createdAt,
        )
}