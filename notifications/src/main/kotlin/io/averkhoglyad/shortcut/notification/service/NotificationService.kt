package io.averkhoglyad.shortcut.notification.service

import io.averkhoglyad.shortcut.common.util.windowed
import io.averkhoglyad.shortcut.notification.data.ChatEventWithMembers
import io.averkhoglyad.shortcut.notification.data.ChatLifecycleEvent
import io.averkhoglyad.shortcut.notification.integration.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.seconds

interface NotificationService {

    fun handleEvent(event: ChatLifecycleEvent)

    fun events(): Flow<List<ChatEventWithMembers>>

}

@Service
class NotificationServiceImpl(
    private val chatRepository: ChatRepository
) : NotificationService {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO) // TODO: Inject scope!!!
    private val eventsFlow: MutableSharedFlow<ChatLifecycleEvent> = MutableSharedFlow()
    private val chunkedFlow: MutableSharedFlow<List<ChatLifecycleEvent>> = MutableSharedFlow()
//    private val chunkedFlow: Flow<List<ChatLifecycleEvent>> = eventsFlow.map { listOf(it) }
//    private val chunkedFlow: Flow<List<ChatLifecycleEvent>> = eventsFlow.windowed(1.seconds) // TODO: Duration must be injected

    init {
        coroutineScope.launch {
            eventsFlow.windowed(1.seconds) // TODO: Duration must be injected
                .collect { events -> chunkedFlow.emit(events) }
        }
    }

    override fun handleEvent(event: ChatLifecycleEvent) {
        coroutineScope.launch {
            eventsFlow.emit(event)
        }
    }

    override fun events(): Flow<List<ChatEventWithMembers>> {
        return chunkedFlow
            .map { populateWithMembers(it) }
    }

    private fun populateWithMembers(events: List<ChatLifecycleEvent>): List<ChatEventWithMembers> {
        val membersByChatId = chatRepository.members(events.map { it.chat.id })
        return events
            .map {
                ChatEventWithMembers(
                    event = it,
                    members = membersByChatId[it.chat.id] ?: emptyList(),
                )
            }
    }
}
