package io.averkhoglyad

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.time.delay
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.pathVariableOrNull
import reactor.core.publisher.Mono
import java.time.Duration
import kotlin.coroutines.cancellation.CancellationException

fun main(args: Array<String>) {
    SpringApplication.run(FluxTestApp::class.java, *args)
}

@SpringBootApplication
class FluxTestApp {

    private val log = LoggerFactory.getLogger(FluxTestApp::class.java)

    @Bean
    fun webFluxConfigurer(): WebFluxConfigurer {
        return object : WebFluxConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**").allowedOrigins("*")
            }
        }
    }

    @Bean
    fun routes(): RouterFunction<ServerResponse> {
        return route()
            .GET("/sse/{count}") { req: ServerRequest -> this.handleSse(req) }
            .GET("/sse") { req: ServerRequest -> this.handleSse(req) }
            .build()
    }

    fun handleSse(req: ServerRequest): Mono<ServerResponse> {
        val countLimit = req.pathVariableOrNull("count")?.toInt() ?: -1
        val publisher = produce()
            .let { if (countLimit > 0) it.take(countLimit) else it }
            .onStart { log.info("started") }
            .onEach { log.debug("received {}", it) }
            .onCompletion { err ->
                val message = err
                    ?.let { if (err is CancellationException) "canceled" else "failed: ${err.message}" }
                    ?: "completed"
                log.info(message)
            }

        return ServerResponse.ok()
            .contentType(MediaType.TEXT_EVENT_STREAM)
            .body(publisher, Event::class.java)
    }
}

private fun produce(): Flow<Event> {
    var counter = 0L
    return flow {
        while (currentCoroutineContext().isActive) {
            delay(Duration.ofSeconds(1))
            emit(counter++.let { Event(it, "#$it") })
        }
    }
}

data class Event(
    val inc: Long,
    val message: String
)
