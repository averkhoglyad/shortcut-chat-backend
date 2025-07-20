package io.averkhoglyad.shortcut.users

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UsersApp

fun main(args: Array<String>) {
    runApplication<UsersApp>(*args)
}
