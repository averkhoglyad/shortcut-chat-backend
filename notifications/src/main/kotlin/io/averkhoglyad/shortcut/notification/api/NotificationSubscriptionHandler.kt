package io.averkhoglyad.shortcut.notification.api

import com.fasterxml.jackson.annotation.JsonValue
import io.averkhoglyad.shortcut.common.util.info
import io.averkhoglyad.shortcut.common.util.slf4j
import io.averkhoglyad.shortcut.notification.data.ChatLifecycleEvent
import io.averkhoglyad.shortcut.notification.data.DebugEvent
import io.averkhoglyad.shortcut.notification.service.NotificationService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.queryParamOrNull
import reactor.core.publisher.Mono
import java.util.*
import kotlin.coroutines.cancellation.CancellationException

@Component
class NotificationSubscriptionHandler(
    private val service: NotificationService
) {

    private val log by slf4j()

    fun subscribe(req: ServerRequest): Mono<ServerResponse> {
        val userId = req.queryParamOrNull("userId")
            ?.let { UUID.fromString(it) }
            ?: throw IllegalArgumentException("User ID header must be passed")
        val events = service.events()
            .map { events ->
                events
                    .filter { el -> el.event is DebugEvent || el.members.any { it.id == userId } }
                    .map { it.event }
                    .let { EventsResponse(it) }
            }
            .onStart { log.info("started") }
            .onEach { log.info("received {}", it) }
            .onCompletion { err ->
                log.info {
                    err?.let { if (err is CancellationException) "canceled" else "failed: ${err.message}" } ?: "completed"
                }
            }

        return ServerResponse.ok()
            .contentType(MediaType.TEXT_EVENT_STREAM)
            .body(events, EventsResponse::class.java)
    }
}


data class EventsResponse(@JsonValue val events: List<ChatLifecycleEvent>)
