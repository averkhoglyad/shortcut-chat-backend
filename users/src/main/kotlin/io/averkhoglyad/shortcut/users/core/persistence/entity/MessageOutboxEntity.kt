package io.averkhoglyad.shortcut.users.core.persistence.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("message_outbox")
class MessageOutboxEntity : IdentifiedEntity {

    @Id
    override var id: UUID? = null
    var type: String = ""
    var version: String = ""
    var body: ByteArray = byteArrayOf()
    @CreatedDate
    var createdAt: Instant = Instant.MIN
    var publishedAt: Instant? = null

}
