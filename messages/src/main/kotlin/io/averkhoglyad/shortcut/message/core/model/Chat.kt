package io.averkhoglyad.shortcut.message.core.model

import java.util.*

data class Chat(
    val id: UUID,
    val name: String,
)

data class ChatRef(
    val id: UUID,
)
