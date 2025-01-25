package io.averkhoglyad.shortcut.users

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<UsersApp>().with(TestcontainersConfiguration::class).run(*args)
}
