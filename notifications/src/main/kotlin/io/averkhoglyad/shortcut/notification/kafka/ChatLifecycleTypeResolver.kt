package io.averkhoglyad.shortcut.notification.kafka

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.type.TypeFactory
import io.averkhoglyad.shortcut.notification.data.ChatCreated
import io.averkhoglyad.shortcut.notification.data.MessagePublished
import org.apache.kafka.common.header.Headers
import org.springframework.kafka.support.serializer.JsonTypeResolver
import org.springframework.stereotype.Component

@Component
class ChatLifecycleTypeResolver: JsonTypeResolver {

    override fun resolveType(topic: String, data: ByteArray?, headers: Headers): JavaType {
        val typeFactory = TypeFactory.defaultInstance()
        return when (headers.getLastAsString(EVENT_NAME)) {
            "ChatCreated" -> typeFactory.constructType(ChatCreated::class.java)
            "MessagePublished" -> typeFactory.constructType(MessagePublished::class.java)
            else -> typeFactory.constructMapType(Map::class.java, String::class.java, Object::class.java)
        }
    }
}

// TODO: Move to separated module

const val EVENT_ID = "X-Event-Id"
const val EVENT_NAME = "X-Event-Name"
const val EVENT_VERSION = "X-Event-Version"
const val PUBLISHED_AT = "X-Published-At"
const val PUBLISHED_BY = "X-Published-By"

private fun Headers.getLastAsString(name: String): String {
    return lastHeader(name)?.value()?.toString(Charsets.UTF_8) ?: ""
}
