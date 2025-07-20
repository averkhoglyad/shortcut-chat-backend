package io.averkhoglyad.shortcut.common.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    val mutex = Mutex()
    val buffer = mutableListOf<E>()

    suspend fun flushWindow() {
        val result = mutex.withLock {
            val result = buffer.toList()
            buffer.clear()
            result
        }
        result.takeIf { it.isNotEmpty() }
            ?.let{ send(it)}
    }

    val ticker = launch {
        tickerFlow(period, period)
            .takeWhile { this@launch.isActive }
            .onEach {
                flushWindow()
            }
            .onCompletion {
                err -> if (err != null) flushWindow()
            }
            .collect()
    }

    try {
        this@windowed.collect {
            mutex.withLock { buffer.add(it) }
        }
    } finally {
        ticker.cancel()
    }
}
