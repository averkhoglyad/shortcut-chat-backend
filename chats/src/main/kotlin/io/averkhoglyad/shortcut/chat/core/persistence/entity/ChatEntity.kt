package io.averkhoglyad.shortcut.chat.core.persistence.entity

import io.averkhoglyad.shortcut.common.persistence.id.IdentifiedEntity
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.InsertOnlyProperty
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("chats")
class ChatEntity : IdentifiedEntity<UUID> {

    @Id
    override var id: UUID? = null

    var name: String = ""
    @Column("owner_id")
    var owner: AggregateReference<UserEntity, UUID>? = null
    @MappedCollection(idColumn = "chat_id")
    var members: Set<MemberEntity> = emptySet()
    @CreatedDate
    @InsertOnlyProperty
    var createdAt: Instant = Instant.MIN

}