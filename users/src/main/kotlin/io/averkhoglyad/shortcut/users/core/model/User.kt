package io.averkhoglyad.shortcut.users.core.model

import java.util.*

data class User(
    val id: UUID?,
    val name: String,
    val email: String
)