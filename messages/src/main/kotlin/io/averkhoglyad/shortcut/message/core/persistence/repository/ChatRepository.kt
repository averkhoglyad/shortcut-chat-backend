package io.averkhoglyad.shortcut.message.core.persistence.repository

import io.averkhoglyad.shortcut.message.core.persistence.entity.ChatEntity
import org.springframework.data.repository.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface ChatRepository : Repository<ChatEntity, UUID> {

    @Transactional(readOnly = true)
    fun findById(id: UUID): ChatEntity?

    @Transactional
    fun save(chat: ChatEntity): ChatEntity

}