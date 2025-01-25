package io.averkhoglyad.shortcut.users.api.controller

import io.averkhoglyad.shortcut.users.core.data.EntityResult
import io.averkhoglyad.shortcut.users.core.model.User
import io.averkhoglyad.shortcut.users.core.service.UserService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    private val service: UserService
) {

    @GetMapping("/{id}")
    fun find(@PathVariable id: UUID): EntityResult<User> {
        return service.find(id)
    }

    @PostMapping
    fun create(@RequestBody user: User): EntityResult<User> {
        require(user.id == null)
        return service.create(user)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody user: User): EntityResult<User> {
        require(user.id == null || user.id == id)
        return service.update(user.copy(id = id))
    }
}