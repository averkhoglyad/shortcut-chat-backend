package io.averkhoglyad.shortcut.chat.core.persistence.entity

import io.averkhoglyad.shortcut.common.persistence.reference.emptyReference
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("chat_members")
class MemberEntity {

    @Column("chat_id")
    @ReadOnlyProperty
    var chat: AggregateReference<out ChatEntity, out UUID> = emptyReference()
    @Column("user_id")
    var user: AggregateReference<out UserEntity, out UUID> = emptyReference()
    @Column("created_at")
    var createdAt: Instant = Instant.MIN

}