package io.averkhoglyad.shortcut.mail.sender.test

import io.averkhoglyad.shortcut.mail.sender.core.mailer.EmailMessage
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.chunked
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.string

val emailMessages = Arb.bind<List<String>, String, String, EmailMessage>(
    Arb.email().chunked(1..10),
    Arb.string(),
    Arb.string(),
) { to, subject, body ->
    EmailMessage(
        to = to,
        subject = subject,
        body = body
    )
}