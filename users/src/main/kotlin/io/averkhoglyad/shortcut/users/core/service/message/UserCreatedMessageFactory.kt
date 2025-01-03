package io.averkhoglyad.shortcut.users.core.service.message

import io.averkhoglyad.shortcut.users.core.model.Message
import io.averkhoglyad.shortcut.users.core.model.User
import org.springframework.stereotype.Component

@Component
class UserCreatedMessageFactoryImpl : MessageFactory<User, User> {

    override fun create(user: User): Message<User> {
        return Message(
            type = "UserCreated",
            version = "v1",
            body = user
        )
    }
}
