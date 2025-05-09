package io.averkhoglyad.shortcut.message

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MessageApp

fun main(args: Array<String>) {
    runApplication<MessageApp>(*args)
}
