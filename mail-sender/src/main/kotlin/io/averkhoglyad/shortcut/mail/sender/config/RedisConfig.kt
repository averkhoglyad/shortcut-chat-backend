package io.averkhoglyad.shortcut.mail.sender.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@Configuration
@EnableRedisRepositories
//@EnableConfigurationProperties(RedisProperties::class)
class RedisConfig {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any?> =
        RedisTemplate<String, Any?>()
            .apply {
                this.connectionFactory = connectionFactory
            }
}