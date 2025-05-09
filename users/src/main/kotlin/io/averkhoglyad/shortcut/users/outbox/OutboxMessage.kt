package io.averkhoglyad.shortcut.users.outbox

data class OutboxMessage<B>(
    val type: String,
    val version: String,
    val body: B
)