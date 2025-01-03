package io.averkhoglyad.shortcut.mail.sender.api

import org.slf4j.LoggerFactory
import org.springframework.kafka.listener.KafkaListenerErrorHandler
import org.springframework.kafka.listener.ListenerExecutionFailedException
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class ErrorHandler : KafkaListenerErrorHandler  {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleError(message: Message<*>, exception: ListenerExecutionFailedException) {
        logger.error("KafkaListenerErrorHandler: ", exception)
        logger.error("Message wil lbe placed into the dead latter queue: {}", message)
    }
}