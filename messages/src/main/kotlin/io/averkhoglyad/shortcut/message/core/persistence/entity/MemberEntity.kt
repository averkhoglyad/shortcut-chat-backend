package io.averkhoglyad.shortcut.message.core.persistence.entity

import io.averkhoglyad.shortcut.common.persistence.reference.emptyReference
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("chat_members")
class MemberEntity {

    @Column("user_id")
    var user: AggregateReference<out UserEntity, out UUID> = emptyReference()
    @Column("created_at")
    var createdAt: Instant = Instant.MIN

}