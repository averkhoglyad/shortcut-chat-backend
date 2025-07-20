package io.averkhoglyad.shortcut.message.core.persistence.repository

import io.averkhoglyad.shortcut.message.core.persistence.entity.ChatEntity
import io.averkhoglyad.shortcut.message.core.persistence.entity.MessageEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.repository.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface MessageRepository : Repository<MessageEntity, UUID> {

    @Transactional(readOnly = true)
    fun findByChatOrderByIdDesc(chat: AggregateReference<out ChatEntity, out UUID>,
                                pageable: Pageable): List<MessageEntity>

    @Transactional(readOnly = true)
    fun findByChatAndIdLessThanOrderByIdDesc(chat: AggregateReference<out ChatEntity, out UUID>,
                                             afterId: UUID,
                                             pageable: Pageable): List<MessageEntity>

    @Transactional
    fun save(user: MessageEntity): MessageEntity

}