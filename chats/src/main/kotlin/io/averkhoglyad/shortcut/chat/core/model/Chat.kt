package io.averkhoglyad.shortcut.chat.core.model

import java.time.Instant
import java.util.*

data class ChatRequest(
    val name: String,
    val members: Collection<UserRef>,
)

data class ChatDetails(
    val id: UUID,
    val name: String,
    val owner: User?,
    val members: Collection<User>,
    val createdAt: Instant = Instant.MIN,
)

data class ChatListItem(
    val id: UUID,
    val name: String,
    val owner: User?,
    val createdAt: Instant = Instant.MIN,
)

data class ChatMembers(
    val id: UUID,
    val members: Collection<UserRef>,
)
