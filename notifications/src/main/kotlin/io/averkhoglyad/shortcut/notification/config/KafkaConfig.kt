package io.averkhoglyad.shortcut.notification.config

import io.averkhoglyad.shortcut.notification.data.ChatLifecycleEvent
import io.averkhoglyad.shortcut.notification.kafka.ChatLifecycleTypeResolver
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.ssl.SslBundles
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.ContainerCustomizer
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.support.serializer.JsonDeserializer

const val KAFKA_LISTENER_CHAT_LIFECYCLE = "KAFKA_LISTENER_CHAT_LIFECYCLE"

@Configuration
class KafkaConfig(
    private val configurer: ConcurrentKafkaListenerContainerFactoryConfigurer,
    private val kafkaContainerCustomizer: ObjectProvider<ContainerCustomizer<Any?, Any?, ConcurrentMessageListenerContainer<Any?, Any?>?>>,
    private val kafkaProperties: KafkaProperties,
    private val chatLifecycleTypeResolver: ChatLifecycleTypeResolver,
    private val sslBundles: SslBundles,
) {

    @Bean(KAFKA_LISTENER_CHAT_LIFECYCLE)
    fun taskLifecycleKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, ChatLifecycleEvent> {
        val consumerFactory = chatLifecycleConsumerFactory()
        return kafkaListenerContainerFactory(consumerFactory)
    }

    private fun chatLifecycleConsumerFactory(): ConsumerFactory<String, ChatLifecycleEvent> = consumerFactory {
        val jsonDeserializer = JsonDeserializer<ChatLifecycleEvent>()
        jsonDeserializer.setTypeResolver(chatLifecycleTypeResolver)
        setValueDeserializerSupplier { jsonDeserializer }
    }

    private inline fun <K, V> consumerFactory(crossinline cb: DefaultKafkaConsumerFactory<K, V>.() -> Unit = {}): ConsumerFactory<K, V> {
        val consumerFactory = DefaultKafkaConsumerFactory<K, V>(kafkaProperties.buildConsumerProperties(sslBundles))
        consumerFactory.cb()
        return consumerFactory
    }


    private fun <K, V> kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<K, V>): ConcurrentKafkaListenerContainerFactory<K, V> {
        val factory = ConcurrentKafkaListenerContainerFactory<K, V>()
        @Suppress("UNCHECKED_CAST")
        configurer
            .configure(
                factory as ConcurrentKafkaListenerContainerFactory<Any?, Any?>,
                consumerFactory as ConsumerFactory<Any?, Any?>
            )
        kafkaContainerCustomizer
            .ifAvailable { factory.setContainerCustomizer(it) }
        return factory
    }
}