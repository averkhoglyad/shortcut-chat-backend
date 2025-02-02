package io.averkhoglyad.shortcut.mail.sender.core.persistence

import org.springframework.data.repository.Repository

interface MessageRepository: Repository<MessageEntity, String> {

    fun findById(id: String): MessageEntity?

    fun save(message: MessageEntity): MessageEntity

    fun deleteById(id: String)
}
