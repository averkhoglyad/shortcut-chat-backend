package io.averkhoglyad.shortcut.users.mailer

// TODO: Must be moved to email sender api module

/**
 * Minimal email message model
 */
data class EmailMessage(
    val to: List<String>,
    val subject: String,
    val body: String
)
