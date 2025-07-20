package io.averkhoglyad.shortcut.message.core.persistence.repository

import io.averkhoglyad.shortcut.common.test.asInstantColumn
import io.averkhoglyad.shortcut.common.test.betweenInclusive
import io.averkhoglyad.shortcut.common.test.executeSql
import io.averkhoglyad.shortcut.message.TestcontainersConfiguration
import io.averkhoglyad.shortcut.message.config.PersistenceConfig
import io.averkhoglyad.shortcut.message.core.persistence.entity.UserEntity
import io.averkhoglyad.shortcut.message.core.persistence.repository.util.loadUserRow
import io.averkhoglyad.shortcut.message.core.persistence.repository.util.userEntities
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.date.shouldBeCloseTo
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.uuid
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.simple.JdbcClient
import java.time.Instant
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

private const val INIT = "/repository/user/init.sql"
private const val CLEAR = "/repository/user/clear.sql"

@DataJdbcTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestcontainersConfiguration::class, PersistenceConfig::class)
class UserRepositoryTest(
    val target: UserRepository,
    val jdbc: JdbcClient,
) : FreeSpec({

    "save" - {
        beforeTest { executeSql(INIT) }
        afterTest { executeSql(CLEAR) }

        "insert new User correctly" {
            checkAll(Arb.Companion.userEntities()) { givenEntity ->
                // given
                givenEntity.externalId = UUID.randomUUID().toString() // force unique value
                val givenExternalId = givenEntity.externalId
                val givenName = givenEntity.name
                val givenEmail = givenEntity.email
                val givenIsDeleted = givenEntity.deleted

                // when
                val beforeSave = Instant.now()
                val result = target.save(givenEntity)
                val afterSave = Instant.now()

                // then
                val persistedId = result.id
                persistedId.shouldNotBeNull()
                result.lastSyncAt shouldBe betweenInclusive(beforeSave, afterSave)

                jdbc.loadUserRow(persistedId).shouldNotBeNull {
                    this shouldContain ("external_id" to givenExternalId)
                    this shouldContain ("name" to givenName)
                    this shouldContain ("email" to givenEmail)
                    this shouldContain ("is_deleted" to givenIsDeleted)
                    this["last_sync_at"]
                        .shouldNotBeNull()
                        .asInstantColumn()
                        .shouldBeCloseTo(result.lastSyncAt, 1.milliseconds)
                }
            }
        }

        "update exists User correctly" {
            checkAll(Arb.userEntities(ids = existsUserIds.exhaustive())) { givenEntity ->
                // given
                val givenId = givenEntity.id!!
                val givenName = givenEntity.name
                val givenEmail = givenEntity.email
                val givenIsDeleted = givenEntity.deleted

                // when
                val beforeSave = Instant.now()
                val result = target.save(givenEntity)
                val afterSave = Instant.now()

                // then
                result.id shouldNotBeNull { this shouldBeEqual givenId }
                result.lastSyncAt shouldBe betweenInclusive(beforeSave, afterSave)

                jdbc.loadUserRow(givenId).shouldNotBeNull {
                    this shouldContain ("name" to givenName)
                    this shouldContain ("email" to givenEmail)
                    this shouldContain ("is_deleted" to givenIsDeleted)
                    this["last_sync_at"]
                        .shouldNotBeNull()
                        .asInstantColumn()
                        .shouldBeCloseTo(result.lastSyncAt, 1.milliseconds)
                }
            }
        }

        "update doesn't change externalId" {
            checkAll(existsUserIds.exhaustive()) { givenId ->
                // given
                val originalRow = jdbc.loadUserRow(givenId)!!
                val originalExternalId = originalRow["external_id"] as String
                val givenEntity = UserEntity().apply(originalRow)

                // when
                target.save(givenEntity)

                // then
                jdbc.loadUserRow(givenId)
                    .shouldNotBeNull {
                        this shouldContain ("external_id" to originalExternalId)
                    }
            }
        }
    }

    "findById" - {
        beforeTest { executeSql(INIT) }
        afterTest { executeSql(CLEAR) }

        "loads User correctly" {
            checkAll(existsUserIds.exhaustive()) { givenId ->
                val result = target.findById(givenId)
                result.shouldNotBeNull()

                val row = jdbc.loadUserRow(givenId)!!
                result.id shouldBe givenId
                result.externalId shouldBeEqual row["external_id"] as String
                result.name shouldBeEqual row["name"] as String
                result.email shouldBeEqual row["email"] as String
                result.deleted shouldBeEqual row["is_deleted"] as Boolean
                result.lastSyncAt.shouldBeCloseTo(row["last_sync_at"]!!.asInstantColumn(), 1.milliseconds)
            }
        }

        "returns null if User is not exists" {
            checkAll(Arb.uuid()) { givenId ->
                target.findById(givenId)
                    .shouldBeNull()
            }
        }
    }
})

private val existsUserIds = listOf(
    UUID.fromString("10000000-0000-0000-0000-000000000000"),
    UUID.fromString("10000000-0000-0000-0000-000000000001")
)

private fun UserEntity.apply(row: Map<String, Any?>): UserEntity {
    id = row["id"] as UUID
    name = row["name"] as String
    email = row["email"] as String
    deleted = row["is_deleted"] as Boolean
    externalId = UUID.randomUUID().toString()
    return this
}
