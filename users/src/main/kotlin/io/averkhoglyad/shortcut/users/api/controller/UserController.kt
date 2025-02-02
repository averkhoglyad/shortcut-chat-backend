package io.averkhoglyad.shortcut.users.api.controller

import io.averkhoglyad.shortcut.users.api.util.unwrap
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
    fun find(@PathVariable id: UUID): User {
        return service.find(id)
            .unwrap()
    }

    @PostMapping
    fun create(@RequestBody user: User): User {
        require(user.id == null)
        return service.create(user)
            .unwrap()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody user: User): User {
        require(user.id == null || user.id == id)
        return service.update(user.copy(id = id))
            .unwrap()
    }
}