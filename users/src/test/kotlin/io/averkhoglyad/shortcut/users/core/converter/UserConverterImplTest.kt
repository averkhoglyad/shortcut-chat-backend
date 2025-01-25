package io.averkhoglyad.shortcut.users.core.converter

import io.averkhoglyad.shortcut.users.core.persistence.entity.UserEntity
import io.averkhoglyad.shortcut.users.test.gen.userEntities
import io.averkhoglyad.shortcut.users.test.gen.users
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.checkAll

class UserConverterImplTest : FreeSpec({

    val converter = UserConverterImpl()

    "toEntity" - {
        "returns new UserEntity with correct values" {
            checkAll(users) { user ->
                val result = converter.toEntity(user)
                result.id shouldBe user.id
                result.name shouldBe user.name
                result.email shouldBe user.email
            }
        }
        
        "applies to UserEntity and returns it with correct values" {
            checkAll(users) { user ->
                val source = UserEntity()
                val result = converter.toEntity(user, source)
                result shouldBeSameInstanceAs source
                result.id shouldBe user.id
                result.name shouldBe user.name
                result.email shouldBe user.email
            }
        }
    }

    "fromEntity" - {
        "returns new User with correct values" {
            checkAll(userEntities) { entity ->
                val result = converter.fromEntity(entity)
                result.id shouldBe entity.id
                result.name shouldBe entity.name
                result.email shouldBe entity.email
            }
        }
    }
})
