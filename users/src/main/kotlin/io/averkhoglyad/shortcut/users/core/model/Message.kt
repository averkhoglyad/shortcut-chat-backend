package io.averkhoglyad.shortcut.users.core.model

data class Message<B>(
    val type: String,
    val version: String,
    val body: B
)
