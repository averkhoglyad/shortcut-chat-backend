package io.averkhoglyad.shortcut.users.core.service

import io.averkhoglyad.shortcut.users.core.converter.UserConverter
import io.averkhoglyad.shortcut.users.core.model.User
import io.averkhoglyad.shortcut.users.core.persistence.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val repository: UserRepository,
    private val converter: UserConverter
) {

    @Transactional(readOnly = true)
    fun find(id: UUID): User {
        var entity = repository.findById(id)
            ?: throw RuntimeException("User with id $id not found")
        return converter.fromEntity(entity)
    }

    @Transactional
    fun create(user: User): User {
        require(user.id == null)

        val entity = converter.toEntity(user)
        val saved = repository.save(entity)
        return converter.fromEntity(saved)
    }

    @Transactional
    fun update(user: User): User {
        requireNotNull(user.id)

        val entity = repository.findById(user.id)
            ?.let { converter.toEntity(user, it) }
            ?: throw RuntimeException("User with id ${user.id} not found")

        val saved = repository.save(entity)
        return converter.fromEntity(saved)
    }
}