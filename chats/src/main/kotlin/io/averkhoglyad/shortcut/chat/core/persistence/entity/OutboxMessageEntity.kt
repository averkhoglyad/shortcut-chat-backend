package io.averkhoglyad.shortcut.chat.core.persistence.entity

import io.averkhoglyad.shortcut.common.persistence.id.IdentifiedEntity
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("message_outbox")
class OutboxMessageEntity : IdentifiedEntity<UUID> {

    @Id
    override var id: UUID? = null
    var type: String = ""
    var key: String? = null
    var version: String = ""
    var body: ByteArray = byteArrayOf()
    @CreatedDate
    var createdAt: Instant = Instant.MIN
    var publishedAt: Instant? = null

}
