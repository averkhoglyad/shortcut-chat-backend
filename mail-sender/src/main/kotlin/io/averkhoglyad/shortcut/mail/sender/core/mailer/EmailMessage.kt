package io.averkhoglyad.shortcut.mail.sender.core.mailer

/**
 * Minimal email message model
 */
data class EmailMessage(
    val to: List<String>,
    val subject: String,
    val body: String
)