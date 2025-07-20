package io.averkhoglyad.shortcut.chat.core.model

import java.time.Instant
import java.util.*

data class ChatCreatedEvent(
    val id: UUID,
    val name: String,
    val owner: UserRef?,
    val members: Collection<UserRef>,
    val createdAt: Instant,
)
