package io.averkhoglyad.shortcut.notification.service

import io.averkhoglyad.shortcut.common.util.windowed
import io.averkhoglyad.shortcut.notification.data.ChatEventWithMembers
import io.averkhoglyad.shortcut.notification.data.ChatLifecycleEvent
import io.averkhoglyad.shortcut.notification.integration.ChatRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import kotlin.time.toKotlinDuration

interface NotificationService {

    suspend fun handleEvent(event: ChatLifecycleEvent)

    fun events(): Flow<List<ChatEventWithMembers>>

}

@Service
class NotificationServiceImpl(
    private val chatRepository: ChatRepository,
    @Value("\${notifications.window-duration}")
    private val windowDuration: Duration,
) : NotificationService {

    private val eventsFlow: MutableSharedFlow<ChatLifecycleEvent> = MutableSharedFlow()
    private val chunkedFlow: Flow<List<ChatLifecycleEvent>> = eventsFlow
        .windowed(windowDuration.toKotlinDuration())

    override suspend fun handleEvent(event: ChatLifecycleEvent) {
        eventsFlow.emit(event)
    }


    @ExperimentalCoroutinesApi
    override fun events(): Flow<List<ChatEventWithMembers>> {
        return chunkedFlow
            .flatMapConcat { populateWithMembers(it) }
    }

    private fun populateWithMembers(events: List<ChatLifecycleEvent>): Flow<List<ChatEventWithMembers>> =
        chatRepository.members(events.map { it.chat.id }.distinct())
            .map { membersByChatId ->
                events.map { evt ->
                    ChatEventWithMembers(
                        event = evt,
                        members = membersByChatId[evt.chat.id] ?: emptyList(),
                    )
                }
            }
}
