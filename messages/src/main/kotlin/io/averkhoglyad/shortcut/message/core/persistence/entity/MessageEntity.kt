package io.averkhoglyad.shortcut.message.core.persistence.entity

import io.averkhoglyad.shortcut.common.persistence.id.IdentifiedEntity
import io.averkhoglyad.shortcut.common.persistence.reference.emptyReference
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.InsertOnlyProperty
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("messages")
class MessageEntity: IdentifiedEntity<UUID> {

    @Id
    override var id: UUID? = null

    var text: String = ""
    @Column("author_id")
    var author: AggregateReference<out UserEntity, out UUID> = emptyReference()
    @Column("chat_id")
    var chat: AggregateReference<out ChatEntity, out UUID> = emptyReference()

    @CreatedDate
    @InsertOnlyProperty
    @Column("created_at")
    var createdAt: Instant = Instant.MIN

}