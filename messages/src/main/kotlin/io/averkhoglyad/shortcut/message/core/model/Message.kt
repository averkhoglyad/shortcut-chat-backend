package io.averkhoglyad.shortcut.message.core.model

import java.time.Instant
import java.util.*

data class MessageRequest(
    val text: String,
    val chat: ChatRef,
)

data class Message(
    val id: UUID,
    val text: String,
    val chat: ChatRef,
    val author: UserRef,
    val createdAt: Instant,
)
