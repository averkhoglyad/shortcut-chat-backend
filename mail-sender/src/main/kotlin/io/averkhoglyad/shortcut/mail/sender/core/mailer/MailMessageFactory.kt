package io.averkhoglyad.shortcut.mail.sender.core.mailer

import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Component

interface MailMessageFactory {

    fun create(from: String, message: EmailMessage): SimpleMailMessage

}

@Component
class MailMessageConverterFactoryImpl : MailMessageFactory {

    override fun create(from: String, message: EmailMessage): SimpleMailMessage {
        return SimpleMailMessage().apply {
            this.from = from
            this.setTo(*message.to.toTypedArray())
            this.subject = message.subject
            this.text = message.body
        }
    }
}
