package io.averkhoglyad.shortcut.users.core.converter

import io.averkhoglyad.shortcut.users.core.model.User
import io.averkhoglyad.shortcut.users.core.persistence.entity.UserEntity
import org.springframework.stereotype.Component

interface UserConverter {

    fun toEntity(user: User): UserEntity

    fun toEntity(user: User, entity: UserEntity): UserEntity

    fun fromEntity(entity: UserEntity): User

}

@Component
class UserConverterImpl : UserConverter {

    override fun toEntity(user: User): UserEntity = toEntity(user, UserEntity())

    override fun toEntity(user: User, entity: UserEntity): UserEntity {
        return entity.apply {
            id = user.id
            name = user.name
            email = user.email
        }
    }

    override fun fromEntity(entity: UserEntity): User {
        return User(
            id = entity.id,
            name = entity.name,
            email = entity.email
        )
    }
}