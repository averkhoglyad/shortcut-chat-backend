package io.averkhoglyad

import org.apache.http.entity.ContentType
import us.abstracta.jmeter.javadsl.JmeterDsl.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

private val now = LocalDateTime.now()
private val inc = AtomicInteger(100_000)

fun main() {
    val threadGroup = rpsThreadGroup()
        .maxThreads(500)
        .rampTo(10.0, Duration.ofSeconds(5))
        .rampTo(100.0, Duration.ofSeconds(5))
        .rampTo(1000.0, Duration.ofSeconds(5))
        .rampToAndHold(5_000.0, Duration.ofSeconds(15), Duration.ofSeconds(20))
        .rampTo(0.0, Duration.ofSeconds(5))
        .children(
            httpDefaults().url("http://127.0.0.1:8000"),
//            httpSampler("/actuator/health")
            httpSampler("/users")
                .post({ generateUserCreateRequestBody() }, ContentType.APPLICATION_JSON),
        )

    testPlan(threadGroup)
        .run()
}

private fun generateUserCreateRequestBody(): String = inc.getAndIncrement()
    .let { i -> "{ \"name\": \"user-${now}-${i}\", \"email\": \"user_${now}_${i}@fake.mail\" }" }