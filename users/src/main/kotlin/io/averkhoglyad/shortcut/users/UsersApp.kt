package io.averkhoglyad.shortcut.users

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.Arrays

@SpringBootApplication
class UsersApp

fun main(args: Array<String>) {
    runApplication<UsersApp>(*args)
}
