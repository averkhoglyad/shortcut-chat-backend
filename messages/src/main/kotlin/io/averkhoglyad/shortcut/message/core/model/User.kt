package io.averkhoglyad.shortcut.message.core.model

import java.util.*

data class User(
    val id: UUID,
    val name: String = "",
    val email: String = "",
)

data class UserRef(val id: UUID)
