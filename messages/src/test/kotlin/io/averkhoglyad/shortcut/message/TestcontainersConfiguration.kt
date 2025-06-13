package io.averkhoglyad.shortcut.message

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.kafka.ConfluentKafkaContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:14-alpine"))

    @Bean
    @ServiceConnection
    fun kafkaContainer(): ConfluentKafkaContainer = ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.8.0"))
        .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
        .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
        .withEnv("KAFKA_MIN_INSYNC_REPLICAS", "1")
        .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
        .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")

    @Bean
    @Scope("prototype")
    fun kafkaConsumer(kafka: ConfluentKafkaContainer): KafkaConsumer<String, String> {
        val props = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafka.bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to "test-group",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java
        )
        return KafkaConsumer(props)
    }

    @Bean
    fun kafkaConsumerProvider(kafka: ConfluentKafkaContainer): (List<String>) -> KafkaConsumer<String, String> {
        return { topics ->
            this.kafkaConsumer(kafka)
                .apply{ subscribe(topics) }
        }
    }
}
