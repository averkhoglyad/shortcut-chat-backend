package io.averkhoglyad.shortcut.notification.config

import io.averkhoglyad.shortcut.common.util.tickerFlow
import io.averkhoglyad.shortcut.notification.data.DebugEvent
import io.averkhoglyad.shortcut.notification.service.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.seconds

@Configuration
@ConditionalOnProperty("application.debug.events.enabled")
class DebugConfig(
    private val service: NotificationService,
) {

    init {
        CoroutineScope(SupervisorJob()).launch {
            tickerFlow(5.seconds)
                .collect { service.handleEvent(DebugEvent) }
        }
    }
}