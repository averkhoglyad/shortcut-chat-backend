package io.averkhoglyad.shortcut.mail.sender

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MailSenderApp

fun main(args: Array<String>) {
    runApplication<MailSenderApp>(*args)
}
