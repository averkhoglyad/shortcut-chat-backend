package io.averkhoglyad.shortcut.notification.data

import java.util.*

data class ChatMembers(
    val id: UUID,
    val members: Collection<UserRef>,
)
