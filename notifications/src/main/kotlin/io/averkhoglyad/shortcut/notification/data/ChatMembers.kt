package io.averkhoglyad.shortcut.notification.data

import java.util.UUID

data class ChatMembers(
    val id: UUID,
    val members: Collection<UserRef>,
)
