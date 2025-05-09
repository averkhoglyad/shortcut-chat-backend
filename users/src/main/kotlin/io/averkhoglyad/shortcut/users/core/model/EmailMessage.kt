package io.averkhoglyad.shortcut.users.core.model

/**
 * Minimal email message model
 */
data class EmailMessage(
    val to: List<String>,
    val subject: String,
    val body: String
)