package io.averkhoglyad.shortcut.users.test.gen

import io.averkhoglyad.shortcut.users.core.model.User
import io.averkhoglyad.shortcut.users.core.persistence.entity.UserEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uuid

val users: Arb<User> = Arb.bind(
    Arb.uuid(),
    Arb.string(),
    Arb.email()
) { id, name, email ->
    User(id = id, name = name, email = email)
}

val userEntities: Arb<UserEntity> = Arb.bind(
    Arb.uuid(),
    Arb.string(),
    Arb.email()
) { id, name, email ->
    UserEntity().apply {
        this.id = id
        this.name = name
        this.email = email
    }
}