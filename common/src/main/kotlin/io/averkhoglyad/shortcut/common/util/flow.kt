package io.averkhoglyad.shortcut.common.util

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration

fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
    delay(initialDelay)
    while (coroutineContext.isActive) {
        emit(Unit)
        delay(period)
    }
}

fun <E> Flow<E>.windowed(period: Duration): Flow<List<E>> = channelFlow {
    var windowJob: Job? = null
    val buffer = mutableListOf<E>()

    suspend fun flushWindow() {
        if (buffer.isNotEmpty()) {
            send(buffer.toList())
            buffer.clear()
        }
        windowJob?.cancel()
        windowJob = null
    }

    fun collectValue(value: E) {
        windowJob = windowJob ?: launch {
            delay(period)
            flushWindow()
        }
        buffer.add(value)
    }

    try {
        this@windowed.collect { collectValue(it) }
    } finally {
        flushWindow()
    }
}
