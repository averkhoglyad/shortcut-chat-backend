package io.averkhoglyad.users

import io.averkhoglyad.shortcut.users.UsersApp
import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<UsersApp>().with(TestcontainersConfiguration::class).run(*args)
}
