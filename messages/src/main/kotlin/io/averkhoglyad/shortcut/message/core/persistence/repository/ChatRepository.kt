package io.averkhoglyad.shortcut.message.core.persistence.repository

import io.averkhoglyad.shortcut.message.core.persistence.entity.ChatEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface ChatRepository : Repository<ChatEntity, UUID> {

    @Transactional(readOnly = true)
    fun findById(id: UUID): ChatEntity?

    @Transactional
    fun save(chat: ChatEntity): ChatEntity

}