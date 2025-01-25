package io.averkhoglyad.shortcut.users.core.persistence.repository

import io.averkhoglyad.shortcut.users.core.persistence.entity.MessageOutboxEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.*

interface MessageOutboxRepository: Repository<MessageOutboxEntity, UUID> {

    fun save(user: MessageOutboxEntity): MessageOutboxEntity

    fun findByCreatedAtLessThanAndPublishedAtIsNullOrderByCreatedAt(before: Instant,
                                                                    pageable: Pageable): List<MessageOutboxEntity>

    @Modifying
    @Query("UPDATE message_outbox SET published_at=now() WHERE id IN (:ids)")
    fun markAsPublishedByIdIsIn(@Param("ids") ids: List<UUID>)

    fun deleteByPublishedAtLessThan(before: Instant)

}