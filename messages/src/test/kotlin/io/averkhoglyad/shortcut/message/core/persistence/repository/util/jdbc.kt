package io.averkhoglyad.shortcut.message.core.persistence.repository.util

import org.springframework.jdbc.core.simple.JdbcClient
import java.util.UUID


fun JdbcClient.loadUserRow(id: UUID): Map<String, Any?>? =
    this.sql("select * from users where id=:id")
        .param("id", id)
        .query()
        .listOfRows()
        .firstOrNull()

fun JdbcClient.loadChatRow(id: UUID): Map<String, Any?>? =
    this.sql("select * from chats where id=:id")
        .param("id", id)
        .query()
        .listOfRows()
        .firstOrNull()

fun JdbcClient.loadChatMemberRows(chatId: UUID): List<Map<String, Any?>> =
    this.sql("select * from chat_members where chat_id=:chatId")
        .param("chatId", chatId)
        .query()
        .listOfRows()

fun JdbcClient.countChatMessageRows(chatId: UUID): Long =
    this.sql("select count(*) from messages where chat_id=:chatId")
        .param("chatId", chatId)
        .query(Long::class.java)
        .single()

fun JdbcClient.loadChatMessageRows(chatId: UUID): List<Map<String, Any?>> =
    this.sql("select * from messages where chat_id=:chatId")
        .param("chatId", chatId)
        .query()
        .listOfRows()

fun JdbcClient.loadMessageRow(id: UUID): Map<String, Any?>? =
    this.sql("select * from messages where id=:id")
        .param("id", id)
        .query()
        .listOfRows()
        .firstOrNull()
